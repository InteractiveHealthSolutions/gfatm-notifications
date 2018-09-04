package com.ihsinformatics.gfatmnotifications.email.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.joda.time.DateTime;

import com.ihsinformatics.emailer.EmailEngine;
import com.ihsinformatics.gfatmnotifications.email.DatabaseConnection;
import com.ihsinformatics.gfatmnotifications.email.model.ChilhoodFact;
import com.ihsinformatics.gfatmnotifications.email.model.Email;
import com.ihsinformatics.gfatmnotifications.email.model.FastFact;
import com.ihsinformatics.gfatmnotifications.email.model.PetFact;
import com.ihsinformatics.gfatmnotifications.email.service.EmailService;
import com.ihsinformatics.gfatmnotifications.email.util.HtmlUtil;
import com.ihsinformatics.gfatmnotifications.email.util.OpenMrsUtil;

public class GfatmEmailJob implements EmailService {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private DateTime dateFrom;
	private OpenMrsUtil openMrsUtil;
	private Properties props;
	private String subjectpet, subjectfast, subjectchildhood, from, businesAnalystEmail;
	private String gfatmConcernPerson;
	private String bodyMessage;
	public SimpleDateFormat DATE_FORMATWH = new SimpleDateFormat("yyyy-MM-dd");
	private String developerEmail = "shujaat.ali@ihsinformatics.com";

	@Override
	public void initializeProperties() {
		props = DatabaseConnection.props;
		dateFrom = new DateTime().minusDays(1);
		subjectfast = props.getProperty("mail.subject.title");
		subjectpet = props.getProperty("mail.pet.subject.title");
		subjectchildhood = props.getProperty("mail.childhood.subject.title");
		from = props.getProperty("mail.user.username");
		// watcherEmail= props.getProperty("emailer.admin-email");
		businesAnalystEmail = props.getProperty("emailer.busines.analyst.email");
		gfatmConcernPerson = props.getProperty("gfatm.concern.person.name");
		bodyMessage = props.getProperty("mail.body.message");
	}

	@Override
	public void execute(OpenMrsUtil openMrsUtil) {

		this.openMrsUtil = openMrsUtil;
		fastDailyEmailReport(dateFrom);
		log.info("Fast Process is successfully executed...");
		childhoodDailyEmailReport(dateFrom);
		log.info("Chidlhood Process is successfully executed...");
		petDailyEmailReport(dateFrom);
		log.info("Pet Process is successfully executed...");
	}

	/**************** PET EMAIL ***************************/
	private void petDailyEmailReport(DateTime dateFrom) {

		String todayDate = DATE_FORMATWH.format(dateFrom.toDate());
		ArrayList<PetFact> factPet = openMrsUtil.getPetFact(todayDate);
		if (!factPet.isEmpty()) {
			for (PetFact petTable : factPet) {
				Email emailVal = openMrsUtil.getEmailByLocationId(petTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:" + petTable.getLocationDescription()
							+ " have not linked with any site supervisor email ");
				} else {
					petTable.setEmailAddress(emailVal.getEmailAdress());
					petReportMapping(petTable);
				}
			}
		} else {
			sendEmail(businesAnalystEmail, HtmlUtil.getInstance().getMessageFormat(gfatmConcernPerson.toString(),
					bodyMessage.toString(), "PET"), subjectpet);

			log.warning("No updates are avaiable...");
		}

	}

	private boolean petReportMapping(PetFact factPet) {

		String facilityName = factPet.getLocationDescription() + " ( " + factPet.getLocationName() + " )";
		LinkedHashMap<String, Integer> mapping = new LinkedHashMap<String, Integer>();
		mapping.put("No of Index Patient Registered", factPet.getNoOfIndexPatientRegistered());
		mapping.put("No of DSTB patients ", factPet.getNoOfDSTBPatients());
		mapping.put("No of DRTB patients ", factPet.getNoOfDRTBPatients());
		mapping.put("No of Baseline Screening", factPet.getNoOfBaselineScreening());
		mapping.put("No of Index Patients agreed for their contacts' screening", factPet.getNoOfIndexPatientsAgreed());
		mapping.put("No of Adults Contacts", factPet.getNoOfAdultsContacts());
		mapping.put("No of Peads Contacts", factPet.getNoOfPeadsContacts());
		mapping.put("No of index not eligible for study", factPet.getNoOfIndexNotEligibleStudy());
		mapping.put("No of Contact Screening Counseling Done", factPet.getNoOfContactScreeningCounselingDone());
		mapping.put("No of Baseline Counseling Done", factPet.getNoOfBaselineCounselingDone());
		mapping.put("No of Contacts Investigated", factPet.getNoOfContactsInvestigated());
		mapping.put("No of Contacts diagnosed with TB", factPet.getNoOfContactsDiagnosedTB());
		mapping.put("No of contacts eligible for PET", factPet.getNoOfContactsEligiblePET());
		mapping.put("No of contacts agreed for PET", factPet.getNoOfContactsAgreedPET());
		mapping.put("No of Contacts completed treatment", factPet.getNoOfContactsCompletedTreatment());

		String message = HtmlUtil.getInstance().getHtmltableFormat(mapping, facilityName);
		sendEmail(developerEmail, message, subjectpet);
		return sendEmail(factPet.getEmailAddress(), message, subjectpet);
	}

	/**************** Childhood EMAIL ***************************/
	private void childhoodDailyEmailReport(DateTime dateFrom2) {

		String todayDate = DATE_FORMATWH.format(dateFrom.toDate());
		ArrayList<ChilhoodFact> factChildhood = openMrsUtil.getFactChildhood(todayDate);
		if (!factChildhood.isEmpty()) {
			for (ChilhoodFact childhoodTable : factChildhood) {
				Email emailVal = openMrsUtil.getEmailByLocationId(childhoodTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:" + childhoodTable.getLocationDescription()
							+ " have not linked with any site supervisor email.");
				} else {
					childhoodTable.setEmailAddress(emailVal.getEmailAdress());
					childhoodMapping(childhoodTable);
				}
			}
		} else {
			sendEmail(businesAnalystEmail,
					HtmlUtil.getInstance().getMessageFormat(gfatmConcernPerson, bodyMessage.toString(), "Childhood"),
					subjectchildhood);
			log.warning("No updates are avaiable...");
		}
	}

	private boolean childhoodMapping(ChilhoodFact chilhoodFact) {

		String facilityName = chilhoodFact.getLocationDescription() + " ( " + chilhoodFact.getLocationName() + " )";

		LinkedHashMap<String, Integer> mapping = new LinkedHashMap<String, Integer>();
		mapping.put("Screened by nurse", chilhoodFact.getScreenedByNurse());
		mapping.put("Presumptive by nurse", chilhoodFact.getPresumptiveByNurse());
		mapping.put("Presumptive Case Confirmed forms", chilhoodFact.getPresumptiveCaseConfirmedForms());
		mapping.put("TB Presumptive Confirmed", chilhoodFact.getTbPresumptiveConfirmed());
		mapping.put("Test indication ", chilhoodFact.getTestIndication());
		mapping.put("CBC Indicated", chilhoodFact.getCbcIndicated());
		mapping.put("ESR Indicated", chilhoodFact.getEsrIndicated());
		mapping.put("CXR Indicated", chilhoodFact.getCxrIndicated());
		mapping.put("MT Indicated", chilhoodFact.getMtIndicated());
		mapping.put("Ultrasound Indicated", chilhoodFact.getUltrasoundIndicated());
		mapping.put("Histopathology/FNAC Indicated", chilhoodFact.getHistopathologyFNACIndicated());
		mapping.put("CT scan Indicated", chilhoodFact.getCtScanIndicated());
		mapping.put("GXP Indicated", chilhoodFact.getGxpIndicated());
		mapping.put("TB Treatment intiated", chilhoodFact.getTbTreatmentIntiated());
		mapping.put("Antibiotic trial initiated", chilhoodFact.getAntibioticTrialInitiated());
		mapping.put("IPT treatment initiated", chilhoodFact.getIptTreatmentInitiated());
		mapping.put("TB Treatment Follow up forms", chilhoodFact.getTbTreatmentFUP());
		mapping.put("Antibiotic trial Follow up forms", chilhoodFact.getAntibioticTrialFUP());
		mapping.put("IPT follow up forms", chilhoodFact.getIptFUP());
		mapping.put("End of follow up forms", chilhoodFact.getEndOfFUP());

		String message = HtmlUtil.getInstance().getHtmltableFormat(mapping, facilityName);
		sendEmail(developerEmail, message, subjectchildhood);
		return sendEmail(chilhoodFact.getEmailAddress(), message, subjectchildhood);
	}

	/**************** FAST EMAIL ***************************/
	private void fastDailyEmailReport(DateTime dateFrom2) {
		String todayDate = DATE_FORMATWH.format(dateFrom.toDate());
		// First we need to get All the Fast Fact-Table
		ArrayList<FastFact> factFast = openMrsUtil.getFactFast(todayDate);
		if (!factFast.isEmpty()) {
			for (FastFact factTable : factFast) {
				Email emailVal = openMrsUtil.getEmailByLocationId(factTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:" + factTable.getLocationDescription()
							+ " have not linked with any site supervisor email ");
				} else {
					factTable.setEmailAddress(emailVal.getEmailAdress());
					fastMapping(factTable);

				}
			}
		} else {
			// businesAnalystEmail
			sendEmail(businesAnalystEmail,
					HtmlUtil.getInstance().getMessageFormat(gfatmConcernPerson, bodyMessage.toString(), "Fast"),
					subjectfast);
			log.warning("No updates are avaiable...");
		}

	}

	private boolean fastMapping(FastFact factTable) {

		String facilityName = factTable.getLocationDescription() + " ( " + factTable.getLocationName() + " )";

		LinkedHashMap<String, Integer> mapping = new LinkedHashMap<String, Integer>();
		mapping.put("Verbal Screened", factTable.getTotalScreeingForm());
		mapping.put("Chest X-Rays", factTable.getChestXrays());
		mapping.put("Verbal Screen Presumptives", factTable.getVerbalScreenPresumptives());
		mapping.put("Chest X-Ray Presumptives", factTable.getChestXrayPresumptives());
		mapping.put("Samples Collected from Verbal Screen Presumptives",
				factTable.getSamplesCollectedVerbalScreenPresumptives());
		mapping.put("Samples Collected from CXR Presumptives", factTable.getSamplesCollectedCXRPresumptives());
		mapping.put("GXP Tests Done", factTable.getGxpTestsDone());
		mapping.put("Internal Tests", factTable.getInternalTests());
		mapping.put("External Tests", factTable.getExternalTests());
		mapping.put("MTB+ [Internal]", factTable.getmTBpveInternal());
		mapping.put("MTB+ [External]", factTable.getmTBpveExternal());
		mapping.put("MTB+/RR+ [Internal]", factTable.getmTBpveRRpveInternal());
		mapping.put("MTB+/RR+ [External]", factTable.getmTBpveRRpveExternal());
		mapping.put("Error/Invalid/No Result", factTable.getErrorNoResultInvalid());
		mapping.put("Clinically Diagnosed", factTable.getClinicallyDiagnosed());
		mapping.put("Initiated on Antibiotic", factTable.getInitiatedOnAntibiotic());
		mapping.put("Initiated on TB Tx", factTable.getInitiatedOnTBTx());

		String message = HtmlUtil.getInstance().getHtmltableFormat(mapping, facilityName);
		sendEmail(developerEmail, message, subjectfast);
		return sendEmail(factTable.getEmailAddress(), message, subjectfast);
	}

	@Override
	public boolean sendEmail(String emailAdress, String message, String subject) {

		boolean isSent;
		String[] recipient = { emailAdress };
		try {
			isSent = EmailEngine.getInstance().postHtmlMail(recipient, subject, message, from);
		} catch (MessagingException e) {
			e.printStackTrace();
			log.warning("Email not send due to :- " + e.getMessage());
			return false;
		}
		return isSent;
	}

}
