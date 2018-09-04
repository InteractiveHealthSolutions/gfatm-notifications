/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.ValidationUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SmsNotificationsJob implements Job {

	private static final Logger	log	= Logger.getLogger(Class.class.getName());
	private DatabaseUtil		localDb;
	private OpenMrsUtil			openmrs;
	private SmsController		smsController;
	// private DatabaseUtil db;

	private DateTime			dateFrom;
	private DateTime			dateTo;
	public   SimpleDateFormat	DATE_FORMATWH						= new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat	DATE_FORMAT							= new SimpleDateFormat("dd-MMM-yyyy");
	public SmsNotificationsJob() {
	}

	private void initialize(SmsNotificationsJob smsJob) {
		
		setLocalDb(smsJob.getLocalDb());
		setOpenmrs(smsJob.getOpenmrs());
		setDateFrom(smsJob.getDateFrom());
		setDateTo(smsJob.getDateTo());
		setSmsController(smsJob.getSmsController());
	
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap dataMap = context.getMergedJobDataMap();
		SmsNotificationsJob smsJob = (SmsNotificationsJob) dataMap
				.get("smsJob");
		initialize(smsJob);

		// With every trigger we need to fetch all the data
		openmrs.loadLocations();
		openmrs.loadPatients();
		openmrs.loadUsers();
		openmrs.loadEncounterTypes();

		dateFrom = dateFrom.minusHours(24);
		System.out.println(dateFrom + " " + dateTo);

		 executeFastSms(dateFrom, dateTo);
		///executeChildhoodTBSms(dateFrom, dateTo);
		// TODO: executePetSms(dateFrom, dateTo);
		// TODO: executeComorbiditiesSms(dateFrom, dateTo);
		// TODO: executePmdtSms(dateFrom, dateTo);
	}

	private void executeFastSms(DateTime dateFrom, DateTime dateTo) {
		
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (int type : Constants.FAST_ENCOUNTER_TYPE_IDS) {
			List<Encounter> temp = getOpenmrs().getEncounters(dateFrom, dateTo,
					type);
			encounters.addAll(temp);
		}

		// Some encounters will be removed
		List<Encounter> toDelete = new ArrayList<Encounter>();
		for (Encounter encounter : encounters) {
			// Remove encounters with missing or fake mobile numbers
			System.out.println(encounter.getPatientContact());
			if (encounter.getPatientContact() == null) {
				toDelete.add(encounter);
			} else if (!ValidationUtil.isValidContactNumber(encounter
					.getPatientContact())) {
				toDelete.add(encounter);
			}

		}

		encounters.removeAll(toDelete);
		// Get observations against each encounter fetched
		for (Encounter encounter : encounters) {

			Map<String, Object> observations = getObservations(encounter);
			encounter.setObservations(observations);
			// System.out.println(observations);
			switch (encounter.getEncounterType()) {
				case "FAST-AFB Smear Test Order":
					break;
				case "FAST-AFB Smear Test Result":
					break;
				case "FAST-Contact Registry":
					break;
				case "FAST-DST Culture Test Order":
					break;
				case "FAST-DST Culture Test Result":
					break;
				case "FAST-End of Followup":
					break;
				case "FAST-GXP Specimen Collection":
					break;
				case "FAST-GXP Test":
					// sendGeneXpertSms(encounter);
					break;
				case "FAST-Missed Visit Followup":
					break;
				case "FAST-Patient Location":
					break;
				case "FAST-Presumptive":
					break;
				case "FAST-Presumptive Information":
					break;
				case "FAST-Prompt":
					break;
				case "FAST-Referral Form":
					sendReferralFormSms(encounter, smsController);
					break;
				case "FAST-Screening":
					break;
				case "FAST-Screening CXR Test Order":
					break;
				case "FAST-Screening CXR Test Result":
					break;
				case "FAST-Treatment Followup":
					sendTreatmentFollowupSms(encounter, smsController);
					break;
				case "FAST-Treatment Initiation":
					sendTreatmentInitiationSms(encounter, smsController);
					break;
				default:
					// Do nothing
			}
		}
	}

	public Map<String, Object> getObservations(Encounter encounter) {

		Map<String, Object> observations = getOpenmrs()
				.getEncounterObservations(encounter);
		return observations;
	}

	public DatabaseUtil getLocalDb() {
		return localDb;
	}

	public void setLocalDb(DatabaseUtil localDb) {
		this.localDb = localDb;
	}

	public OpenMrsUtil getOpenmrs() {
		return openmrs;
	}

	public void setOpenmrs(OpenMrsUtil openmrs) {
		this.openmrs = openmrs;
	}

	public DateTime getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(DateTime dateFrom) {
		this.dateFrom = dateFrom;
	}

	public DateTime getDateTo() {
		return dateTo;
	}

	public void setDateTo(DateTime dateTo) {
		this.dateTo = dateTo;
	}

	public SmsController getSmsController() {
		return smsController;
	}

	public void setSmsController(SmsController smsController) {
		this.smsController = smsController;
	}

	/**
	 *
	 *
	 * @param encounter
	 * @param smsController
	 * @return
	 */
	public boolean sendReferralFormSms(Encounter encounter,
			SmsController smsController) {

		Map<String, Object> observations = encounter.getObservations();
		DateTime dueDate = new DateTime(encounter.getEncounterDate());
		// check the past date time
		try {

			dueDate = dueDate.plusDays(1);
			Date parsDate = new SimpleDateFormat("dd-MMM-yyyy")
			.parse(DATE_FORMAT.format(dueDate.toDate()));
			Date currentDate = new SimpleDateFormat("dd-MMM-yyyy")
			.parse(DATE_FORMAT.format(new Date()));
			if (parsDate.before(currentDate)) {
				return false;
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// dueDate = DateTime.now();
		String sendTo;
		String siteSupervisorName;

		Object referredOrTransferred = observations.get("referral_transfer");
		if (referredOrTransferred.equals(Constants.PATIENT_REFERRED)
				|| referredOrTransferred.equals(Constants.PATIENT_TRANSFERRED)) {

			String referralSite = observations.get("referral_site").toString();
			Location referralLocation = openmrs
					.getLocationByShortCode(referralSite);

			if (referralLocation == null) {
				return false;
			}
			/**
			 * In case of primary contact is empty then we go with secondary
			 * contact number if we have both are missing then we return false.
			 */
			if (referralLocation.getPrimaryContact() != null) {

				sendTo = referralLocation.getPrimaryContact();
				siteSupervisorName = referralLocation.getPrimaryContactName();

			} else if (referralLocation.getSecondaryContact() != null) {

				sendTo = referralLocation.getPrimaryContact();
				siteSupervisorName = referralLocation.getSecondaryContactName();

			} else {
				return false;
			}
			// create message for site supervisor.
			StringBuilder message = new StringBuilder();
			message.append("Janab " + siteSupervisorName + ",");
			message.append("ap ke markaz " + referralLocation.getName()
					+ " pe aik mareez " + encounter.getIdentifier() + " ");
			message.append("ko muntaqil kiya ja raha hai. Is mareez say rabta karain.");
			try {
				sendTo = sendTo.replace("-", "");
				smsController.createSms(sendTo, message.toString(),
						dueDate.toDate(), Constants.FAST_PROGRAM, "");
				log.info(message.toString());
			} catch (Exception e) {
				log.warning(e.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean sendTreatmentFollowupSms(Encounter encounter,
			SmsController smsController) {
		Date returnVisitDate = null;
		Map<String, Object> observations = encounter.getObservations();
		String returnVisitStr = observations.get("return_visit_date")
				.toString().toUpperCase();
		/************ Conditions ***************/
		if (returnVisitStr == null
				|| !openmrs.isTransferOrReferel(encounter)) {
			return false;
		}
		try {
			// Past date is not allowed so, we ignore or skip the past date
			// notifications
			returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,
					DateTimeUtil.SQL_DATETIME);
			Date currentDate = new SimpleDateFormat("dd-MMM-yyyy")
			.parse(DATE_FORMAT.format(new Date()));

			if (returnVisitDate.before(currentDate)) {
				return false;
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String id = openmrs.checkReferelPresent(encounter);
		Location referralLocation = null;

		if (!id.equals("")) {

			Encounter ency = openmrs.getEncounter(Integer.parseInt(id), 28);
			observations = openmrs.getEncounterObservations(ency);
			ency.setObservations(observations);
			String referralSite = observations.get("referral_site").toString();
			referralLocation = openmrs.getLocationByShortCode(referralSite);
			encounter.setLocation(referralLocation.getName());

		}

		try {
			/*
			 * returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,
			 * DateTimeUtil.SQL_DATETIME);
			 */
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			String sendTo = encounter.getPatientContact();
			sendTo = sendTo.replace("-", "");

			/******** build Custom message.. *****************/
			System.out.print("" + dueDate.getTime());
			StringBuilder message = new StringBuilder();
			message.append("Janab " + encounter.getPatientName() + ",");
			message.append("" + encounter.getLocation()
					+ " pe ap ko doctor ke paas ");
			DATE_FORMAT.applyPattern("EEEE d MMM yyyy");
			message.append(DATE_FORMAT.format(returnVisitDate) + " ");
			message.append("ko moainey aur adwiyaat hasil karne ke liyey tashreef lana hai. ");
			message.append("Agar is mutaliq ap kuch poochna chahain tou AaoTBMitao helpline ");
			message.append("021-111-111-982 pe rabta karain.");
			// send message to patient.
			smsController.createSms(sendTo, message.toString(),
					dueDate.getTime(), Constants.FAST_PROGRAM, "");

			log.info(message.toString());
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean sendTreatmentInitiationSms(Encounter encounter,
			SmsController smsController) {

		Date returnVisitDate = null;
		Map<String, Object> observation = encounter.getObservations();
		System.out.print(observation.toString());
		String rtnVisitDate = observation.get("return_visit_date").toString()
				.toUpperCase();
		String isTbPatient = observation.get("tb_patient").toString()
				.toUpperCase();
		String antibiotic = "";
		Patient patient = openmrs.getPatientByIdentifier(encounter
				.getIdentifier());

		// we need FAST end of follow up form data.
		Map<String, Object> EndOfFollowUpObservation = null;
		Encounter encounterEndFup = openmrs.getEncounterByPatientIdentifier(
				encounter.getIdentifier(),
				Constants.END_FOLLOWUP_FORM_ENCOUNTER_TYPE);
		Map<String, Object> observations = getObservations(encounterEndFup);
		encounterEndFup.setObservations(observations);
		EndOfFollowUpObservation = encounterEndFup.getObservations();
		try {

			returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,
					DateTimeUtil.SQL_DATETIME);

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		/****************** CONDITIONS *************************************/

		if (isTbPatient.equals("INCONCLUSIVE")) {
			antibiotic = observation.get("antibiotic").toString().toUpperCase();
			if (antibiotic.equals("NO")) {
				return false;
			}
		} else if (isTbPatient.equals("YES")) {
			return false;
		}
		if (!EndOfFollowUpObservation.get("treatment_outcome").equals(
				"ANTIBIOTIC COMPLETE - NO TB")) {
			return false;
		}
		// we need to check the treatment_outcome from end of follow of form
		if (rtnVisitDate == null || patient.isDead()) {
			return false;
		}
		// check if the
		if (returnVisitDate.before(new Date())) {
			return false;
		}

		/************************ MAKEUP THE SMS ***************/
		try {
			String sendTo = encounter.getPatientContact();
			// System.out.println(sendTo);
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			Location referralLocation = null;

			/**
			 * Need to check wether the patient have referrel site or not if
			 * patient have referralsite then we set the referral site as a
			 * patient location.
			 */
			String id = openmrs.checkReferelPresent(encounter);
			if (!id.equals("")) {

				Encounter ency = openmrs.getEncounter(Integer.parseInt(id),
						Constants.REFERREL_ENCOUNTER_TYPE);
				observation = openmrs.getEncounterObservations(ency);
				ency.setObservations(observation);
				String referralSite = observation.get("referral_site")
						.toString();
				referralLocation = openmrs.getLocationByShortCode(referralSite);
				encounter.setLocation(referralLocation.getName());

			}
			DATE_FORMAT.applyPattern("EEEE d MMM yyyy");
			/* String df=DateFormat.getDateInstance().format(dueDate.getTime()); */

			/********************* antibiotic = yes then message change ***************/
			StringBuilder message = new StringBuilder();
			if (isTbPatient.equals("INCONCLUSIVE")) {

				message.append("Janab " + encounter.getPatientName() + ", ");
				message.append("" + encounter.getLocation());
				message.append(" pe ap ko doctor ke paas "
						+ DATE_FORMAT.format(returnVisitDate)
						+ " ko moainey ke liyey tashreef lana hai. "
						+ "Agar is mutaliq ap kuch poochna chahain tou AaoTBMitao "
						+ "helpline 021-111-111-982 pe rabta karain.");
			} else {

				message.append("Janab " + encounter.getPatientName() + ", ");
				message.append("" + encounter.getLocation());
				message.append(" pe ap ko doctor ke paas "
						+ DATE_FORMAT.format(returnVisitDate)
						+ "ko moainey aur adwiyaat hasil karne ke liyey "
						+ "tashreef lana hai. Agar is mutaliq ap kuch poochna chahain tou AaoTBMitao "
						+ "helpline 021-111-111-982 pe rabta karain.");
			}

			// sendTo = "03222808980";
			System.out.println(dueDate.getTime());
			sendTo = sendTo.replace("-", "");
			String response = smsController.createSms(sendTo,
					message.toString(), dueDate.getTime(),
					Constants.FAST_PROGRAM, rtnVisitDate);
			System.out.println(response);
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

	/***************************************************************/
	/********************* CHILDHOOD METHODS ***********************/
	/***************************************************************/

	// TODO:complete
	@SuppressWarnings("deprecation")
	public boolean sendTreatmentInitiationSmsChildhood(Encounter encounter) {

		Map<String, Object> observation = encounter.getObservations();
		System.out.print(observation.toString());
		String rtnVisitDate = observation.get("return_visit_date").toString()
				.toUpperCase();

		String iptAcceptance = observation.get("IPT_acceptance").toString()
				.toUpperCase();

		Patient patient = openmrs.getPatientByIdentifier(encounter
				.getIdentifier());

		/****************** conditions *************************************/
		// First Condition
		if (rtnVisitDate.equals(null) || patient.isDead()
				|| !iptAcceptance.equals("YES")) {
			return false;
		}

		Date returnVisitDate;

		// String sendTo = encounter.getPatientContact();
		// System.out.println(sendTo);
		try {
			returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,
					DateTimeUtil.SQL_DATETIME);

			LocalDate duelocalDate = new LocalDate(returnVisitDate);
			duelocalDate.minusDays(1);
			// Date dueDate = duelocalDate.toDate();

			/*
			 * Calendar dueDate = Calendar.getInstance();
			 * dueDate.setTime(returnVisitDate); dueDate.set(Calendar.DATE,
			 * dueDate.get(Calendar.DATE) - 1);
			 */

			// Map<String, Object> observations = encounter.getObservations();
			// Location referralLocation = null;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;

	}

	// TODO:complete
	public boolean sendIptFupSms(Encounter encounter) {

		return true;
	}

	// TODO:complete
	public boolean sendAntibioticTrialFupSms(Encounter encounter) {

		return true;
	}

	// TODO:complete
	public boolean sendReferralSms(Encounter encounter) {

		return true;
	}

	// TODO:complete
	public boolean sendTbTreatmentFupSms() {

		return true;
	}
}
