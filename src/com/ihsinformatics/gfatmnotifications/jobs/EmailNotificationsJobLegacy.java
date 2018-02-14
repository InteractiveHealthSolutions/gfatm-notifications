/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.controllers.EmailController;
import com.ihsinformatics.gfatmnotifications.databaseconnections.Connections;
import com.ihsinformatics.gfatmnotifications.model.ChilhoodFact;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.model.FastFact;
import com.ihsinformatics.gfatmnotifications.model.PetFact;
import com.ihsinformatics.gfatmnotifications.util.HtmlUtile;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;
import com.ihsinformatics.util.DatabaseUtil;

/**
 * @author owais.hussain@ihsinformatics.com and Shujaat.ali@ihsinformatics.com
 *
 */

public class EmailNotificationsJobLegacy {

	private static final Logger	log	= Logger.getLogger(Class.class.getName());
	private DatabaseUtil		warehouseDb;
	private DateTime			dateFrom;
	private OpenMrsUtil			warehouseOpenmrsInstance;
	private EmailController		emailController;
	private Properties			props;

	public EmailNotificationsJobLegacy() {

		props = Connections.prop;
		warehouseDb = UtilityCollection.getWarehouseDb();
		warehouseOpenmrsInstance = new OpenMrsUtil(warehouseDb);
		emailController = new EmailController();
	}

	/*
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute() {

		// load all the site supervisor email from ware database.
		warehouseOpenmrsInstance.LoadAllUsersEmail();
		dateFrom = new DateTime().minusDays(1);
		fastDailyReport(dateFrom);
		log.info("Fast Process is successfully executed...");
		childhoodDailyReport(dateFrom);
		log.info("Chidlhood Process is successfully executed...");
		petDailyReport(dateFrom);
		log.info("Pet Process is successfully executed...");

		try {
			// /when process is complete then close the database conneciton and
			// exit the main thread.
			warehouseDb.closeConnection();
			log.info("Database Connection is Close...");
			System.exit(0);

		} catch (SQLException e) {
			e.printStackTrace();
			log.info("System terminated with exception..." + e.getMessage());
			System.exit(-1);
		}
	}

	/************************* Fast Email Execution *******************/

	public void fastDailyReport(DateTime dateFrom) {

		String todayDate = Constants.DATE_FORMATWH.format(dateFrom.toDate());
		// First we need to get All the Fast Fact-Table
		ArrayList<FastFact> factFast = warehouseOpenmrsInstance
				.getFactFast(todayDate);
		if (!factFast.isEmpty()) {
			for (FastFact factTable : factFast) {
				Email emailVal = warehouseOpenmrsInstance
						.getEmailByLocationId(factTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:"
							+ factTable.getLocationDescription()
							+ " have not linked with any site supervisor email ");
				} else {
					factTable.setEmailAddress(emailVal.getEmailAdress());
					fastSiteSpecificScreenEmail(factTable);
				}
			}
		} else {
			sendEmail(props.getProperty("emailer.admin-email"), HtmlUtile
					.getInstance().getMessageFormate(),
					props.getProperty("mail.subject.title"));
			log.warning("No updates are avaiable...");
		}
	}

	/**
	 * Responsibility is to check the conditions and build the content for
	 * emails to every receipeints
	 * 
	 * @param factTable
	 * @return
	 */
	public boolean fastSiteSpecificScreenEmail(FastFact factTable) {

		String facilityName = factTable.getLocationDescription() + " ( "
				+ factTable.getLocationName() + " )";

		LinkedHashMap<String, Integer> mapping = new LinkedHashMap<String, Integer>();
		mapping.put("Verbal Screened", factTable.getTotalScreeingForm());
		mapping.put("Chest X-Rays", factTable.getChestXrays());
		mapping.put("Verbal Screen Presumptives",
				factTable.getVerbalScreenPresumptives());
		mapping.put("Chest X-Ray Presumptives",
				factTable.getChestXrayPresumptives());
		mapping.put("Samples Collected from Verbal Screen Presumptives",
				factTable.getSamplesCollectedVerbalScreenPresumptives());
		mapping.put("Samples Collected from CXR Presumptives",
				factTable.getSamplesCollectedCXRPresumptives());
		mapping.put("GXP Tests Done", factTable.getGxpTestsDone());
		mapping.put("Internal Tests", factTable.getInternalTests());
		mapping.put("External Tests", factTable.getExternalTests());
		mapping.put("MTB+ [Internal]", factTable.getmTBpveInternal());
		mapping.put("MTB+ [External]", factTable.getmTBpveExternal());
		mapping.put("MTB+/RR+ [Internal]", factTable.getmTBpveRRpveInternal());
		mapping.put("MTB+/RR+ [External]", factTable.getmTBpveRRpveExternal());
		mapping.put("Error/Invalid/No Result",
				factTable.getErrorNoResultInvalid());
		mapping.put("Clinically Diagnosed", factTable.getClinicallyDiagnosed());
		mapping.put("Initiated on Antibiotic",
				factTable.getInitiatedOnAntibiotic());
		mapping.put("Initiated on TB Tx", factTable.getInitiatedOnTBTx());

		String message = HtmlUtile.getInstance().getHtmltableFormate(mapping,
				facilityName);
		sendEmail("shujaat.ali@ihsinformatics.com", message,
				props.getProperty("mail.subject.title"));
		return sendEmail(factTable.getEmailAddress(), message,
				props.getProperty("mail.subject.title"));
	}

	/************************* Childhood Email Execution *******************/

	public void childhoodDailyReport(DateTime dateFrom) {

		String todayDate = Constants.DATE_FORMATWH.format(dateFrom.toDate());
		ArrayList<ChilhoodFact> factChildhood = warehouseOpenmrsInstance
				.getFactChildhood(todayDate);
		if (!factChildhood.isEmpty()) {
			for (ChilhoodFact childhoodTable : factChildhood) {
				Email emailVal = warehouseOpenmrsInstance
						.getEmailByLocationId(childhoodTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:"
							+ childhoodTable.getLocationDescription()
							+ " have not linked with any site supervisor email ");
				} else {
					childhoodTable.setEmailAddress(emailVal.getEmailAdress());
					childhoodSiteSpecificScreenEmail(childhoodTable);
				}
			}
		} else {
			sendEmail(props.getProperty("emailer.admin-email"), HtmlUtile
					.getInstance().getMessageFormate(),
					props.getProperty("mail.childhood.subject.title"));
			log.warning("No updates are avaiable...");
		}
	}

	public boolean childhoodSiteSpecificScreenEmail(ChilhoodFact chilhoodFact) {

		String facilityName = chilhoodFact.getLocationDescription() + " ( "
				+ chilhoodFact.getLocationName() + " )";

		LinkedHashMap<String, Integer> mapping = new LinkedHashMap<String, Integer>();
		mapping.put("Screened by nurse", chilhoodFact.getScreenedByNurse());
		mapping.put("Presumptive by nurse",
				chilhoodFact.getPresumptiveByNurse());
		mapping.put("Screening Location Forms",
				chilhoodFact.getScreeningLocationForms());
		mapping.put("Registration forms ", chilhoodFact.getRegistrationForms());
		mapping.put("Presumptive Case Confirmed forms",
				chilhoodFact.getPresumptiveCaseConfirmedForms());
		mapping.put("TB Presumptive Confirmed",
				chilhoodFact.getTbPresumptiveConfirmed());
		mapping.put("Test indication ", chilhoodFact.getTestIndication());
		mapping.put("CBC Indicated", chilhoodFact.getCbcIndicated());
		mapping.put("ESR Indicated", chilhoodFact.getEsrIndicated());
		mapping.put("CXR Indicated", chilhoodFact.getCxrIndicated());
		mapping.put("MT Indicated", chilhoodFact.getMtIndicated());
		mapping.put("Ultrasound Indicated",
				chilhoodFact.getUltrasoundIndicated());
		mapping.put("Histopathology/FNAC Indicated",
				chilhoodFact.getHistopathologyFNACIndicated());
		mapping.put("CT scan Indicated", chilhoodFact.getCtScanIndicated());
		mapping.put("GXP Indicated", chilhoodFact.getGxpIndicated());
		mapping.put("TB Treatment intiated",
				chilhoodFact.getTbTreatmentIntiated());
		mapping.put("Antibiotic trial initiated",
				chilhoodFact.getAntibioticTrialInitiated());
		mapping.put("IPT treatment initiated",
				chilhoodFact.getIptTreatmentInitiated());
		mapping.put("TB Treatment Follow up forms",
				chilhoodFact.getTbTreatmentFUP());
		mapping.put("Antibiotic trial Follow up forms",
				chilhoodFact.getAntibioticTrialFUP());
		mapping.put("IPT follow up forms", chilhoodFact.getIptFUP());
		mapping.put("End of follow up forms", chilhoodFact.getEndOfFUP());

		String message = HtmlUtile.getInstance().getHtmltableFormate(mapping,
				facilityName);
		sendEmail("shujaat.ali@ihsinformatics.com", message,
				props.getProperty("mail.childhood.subject.title"));
		return sendEmail(chilhoodFact.getEmailAddress(), message,
				props.getProperty("mail.childhood.subject.title"));
	}

	/************************* PET Email Execution *******************/

	public void petDailyReport(DateTime dateFrom) {

		String todayDate = Constants.DATE_FORMATWH.format(dateFrom.toDate());
		ArrayList<PetFact> factPet = warehouseOpenmrsInstance
				.getPetFact(todayDate);
		if (!factPet.isEmpty()) {
			for (PetFact petTable : factPet) {
				Email emailVal = warehouseOpenmrsInstance
						.getEmailByLocationId(petTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:"
							+ petTable.getLocationDescription()
							+ " have not linked with any site supervisor email ");
				} else {
					petTable.setEmailAddress(emailVal.getEmailAdress());
					petSiteDailyReport(petTable);
				}
			}
		} else {
			sendEmail(props.getProperty("emailer.admin-email"), HtmlUtile
					.getInstance().getMessageFormate(),
					props.getProperty("mail.pet.subject.title"));
			log.warning("No updates are avaiable...");
		}

	}

	public boolean petSiteDailyReport(PetFact factPet) {

		String facilityName = factPet.getLocationDescription() + " ( "
				+ factPet.getLocationName() + " )";
		LinkedHashMap<String, Integer> mapping = new LinkedHashMap<String, Integer>();
		mapping.put("No of Index Patient Registered",
				factPet.getNoOfIndexPatientRegistered());
		mapping.put("No of DSTB patients ", factPet.getNoOfDSTBPatients());
		mapping.put("No of DRTB patients ", factPet.getNoOfDRTBPatients());
		mapping.put("No of Baseline Screening",
				factPet.getNoOfBaselineScreening());
		mapping.put(
				"No of Index Patients agreed for their contacts' screening",
				factPet.getNoOfIndexPatientsAgreed());
		mapping.put("No of Adults Contacts", factPet.getNoOfAdultsContacts());
		mapping.put("No of Peads Contacts", factPet.getNoOfPeadsContacts());
		mapping.put("No of index not eligible for study",
				factPet.getNoOfIndexNotEligibleStudy());
		mapping.put("No of Contact Screening Counseling Done",
				factPet.getNoOfContactScreeningCounselingDone());
		mapping.put("No of Baseline Counseling Done",
				factPet.getNoOfBaselineCounselingDone());
		mapping.put("No of Contacts Investigated",
				factPet.getNoOfContactsInvestigated());
		mapping.put("No of Contacts diagnosed with TB",
				factPet.getNoOfContactsDiagnosedTB());
		mapping.put("No of contacts eligible for PET",
				factPet.getNoOfContactsEligiblePET());
		mapping.put("No of contacts agreed for PET",
				factPet.getNoOfContactsAgreedPET());
		mapping.put("No of Contacts completed treatment",
				factPet.getNoOfContactsCompletedTreatment());

		String message = HtmlUtile.getInstance().getHtmltableFormate(mapping,
				facilityName);
		sendEmail("shujaat.ali@ihsinformatics.com", message,
				props.getProperty("mail.pet.subject.title"));
		return sendEmail(factPet.getEmailAddress(), message,
				props.getProperty("mail.pet.subject.title"));
	}

	/******************* Common Methods ************************/

	public boolean sendEmail(String email, String message, String subject) {

		boolean isSent = emailController.sendEmailWithHtml(email, subject,
				message, props.getProperty("mail.user.username"));

		return isSent;
	}

}
