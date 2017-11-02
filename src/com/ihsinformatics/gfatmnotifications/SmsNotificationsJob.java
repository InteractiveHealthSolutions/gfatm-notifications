/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.util.ValidationUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SmsNotificationsJob implements Job {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private DatabaseUtil localDb;
	private OpenMrsUtil openmrs;
	private SmsController smsController;
	private DatabaseUtil db;

	private DateTime dateFrom;
	private DateTime dateTo;

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
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap dataMap = context.getMergedJobDataMap();
		SmsNotificationsJob smsJob = (SmsNotificationsJob) dataMap
				.get("smsJob");
		// dateFrom = new DateTime();
		// dateTo = new DateTime();
		// smsJob.setDateFrom(dateFrom);
		// smsJob.setDateTo(dateTo);
		initialize(smsJob);
		// TODO: Remove the line below on production
		dateFrom = dateFrom.withMonthOfYear(6);
		System.out.println(dateFrom + " " + dateTo);

		executeFastSms(dateFrom, dateTo);

		// TODO: executeChildhoodTBSms(dateFrom, dateTo);
		// TODO: executePetSms(dateFrom, dateTo);
		// TODO: executeComorbiditiesSms(dateFrom, dateTo);
		// TODO: executePmdtSms(dateFrom, dateTo);
	}

	private void executeFastSms(DateTime dateFrom, DateTime dateTo) {
		List<Encounter> encounters = new ArrayList<Encounter>();
		HashSet<Encounter> ecc = new HashSet<Encounter>();
		for (int type : Constants.FAST_ENCOUNTER_TYPE_IDS) {
			List<Encounter> temp = getOpenmrs().getEncounters(dateFrom, dateTo,
					type);
			// ecc.addAll(temp);
			encounters.addAll(temp);
		}

		// Some encounters will be removed
		List<Encounter> toDelete = new ArrayList<Encounter>();
		for (Encounter encounter : encounters) {
			// Remove encounters with missing or fake mobile numbers

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
			/*
			 * Map<String, Object> observations = getOpenmrs()
			 * .getEncounterObservations(encounter);
			 * encounter.setObservations(observations);
			 */
			// System.out.println(encounter.getEncounterId() +" "+
			// encounter.getEncounterType());

			Map<String, Object> observations = getObservations(encounter);
			;
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

	private void executeChildhoodTBSms(DateTime dateFrom, DateTime dateTo) {
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (int type : Constants.CHILDHOOD_TB_ENCOUNTER_TYPE_IDS) {
			List<Encounter> temp = getOpenmrs().getEncounters(dateFrom, dateTo,
					type);
			encounters.addAll(temp);
		}
		// Some encounters will be removed
		List<Encounter> toDelete = new ArrayList<Encounter>();
		for (Encounter encounter : encounters) {
			// Remove encounters with missing or fake mobile numbers

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
			Map<String, Object> observations = getOpenmrs()
					.getEncounterObservations(encounter);
			encounter.setObservations(observations);
			// System.out.println(observations);
			// System.out.println(encounter.getEncounterType());
			switch (encounter.getEncounterType()) {

			case "FAST-Treatment Followup":
				// sendTreatmentFollowupSms(encounter);
				break;
			case "Childhood TB-Treatment Initiation":
				// sendTreatmentInitiationSms(encounter);
				break;
			default:
				// Do nothing
			}
		}
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

	/***************************************************************/
	/** List of methods to execute against each type of encounter **/
	/***************************************************************/

	private void sendGeneXpertSms(Encounter encounter) {
		// Map<String, Object> observations = encounter.getObservations();
		Calendar dueDate = Calendar.getInstance();
		String sendTo = encounter.getPatientContact();
		if (sendTo == null) {
			return;
		}

		StringBuilder message = new StringBuilder();
		message.append("Dear " + encounter.getPatientName() + ", ");
		message.append("your test result is ready for collection at "
				+ encounter.getLocation() + ". ");
		message.append("Please collect at your earliest convenience.");
		try {
			smsController.createSms(sendTo, message.toString(),
					dueDate.getTime(), "FAST", "");
			log.info(message.toString());
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
	}

	public boolean sendReferralFormSms(Encounter encounter, SmsController smsController) {
		Map<String, Object> observations = encounter.getObservations();
		DateTime dueDate = encounter.getEncounterDate();

		dueDate = dueDate.plusDays(1);
		// dueDate = DateTime.now();
		String sendTo;

		Object referredOrTransferred = observations.get("referral_transfer");
		if (referredOrTransferred.equals("PATIENT REFERRED")
				|| referredOrTransferred.equals("PATIENT TRANSFERRED OUT")) {
			String referralSite = observations.get("referral_site").toString();
			Location referralLocation = openmrs.getLocationByShortCode(referralSite);
			ArrayList<String> contact = openmrs.getSiteSupervisorContact(referralSite);
			if (contact.size() < 1) {
				return false;
			}
			for (int i = 0; i < contact.size(); i++) {
				sendTo = contact.get(i);
				StringBuilder message = new StringBuilder();
				message.append("Dear Site Supervisor, patient "
						+ encounter.getPatientId() + ", ");
				message.append("has been transfered/referred to "
						+ referralLocation.getName());
				try {
					sendTo = sendTo.replace("-", "");
					smsController.createSms(sendTo, message.toString(),
							dueDate.toDate(), "FAST", "");
					log.info(message.toString());
				} catch (Exception e) {
					log.warning(e.getMessage());
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean sendTreatmentFollowupSms(Encounter encounter, SmsController smsController) {
		Map<String, Object> observations = encounter.getObservations();
		String returnVisitStr = observations.get("return_visit_date")
				.toString().toUpperCase();
		// || !open.getfilter(encounter,"FAST-End of Followup")
		if (returnVisitStr.equals(null) /*|| open.isTransferOrReferel(encounter)*/) {
			return false;
		}
		String id = openmrs.checkReferelPresent(encounter);
		Location referralLocation = null;
		if (!id.equals("")) {
			Encounter ency = openmrs.getEncounter(Integer.parseInt(id), 28);
			observations = openmrs.getEncounterObservations(ency);
			ency.setObservations(observations);
			String referralSite = observations.get("referral_site").toString();
			referralLocation = openmrs.getLocationByShortCode(referralSite);
		}
		Date returnVisitDate;
		try {
			returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,
					DateTimeUtil.SQL_DATETIME);
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			encounter.setLocation(referralLocation.getName());
			String sendTo = encounter.getPatientContact();
			sendTo = sendTo.replace("-", "");
			StringBuilder message = new StringBuilder();
			message.append("Dear " + encounter.getPatientName() + ", ");
			message.append("please come to " + encounter.getLocation() + " on ");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			sdf.applyPattern("EEEE d MMM yyyy");
			message.append(sdf.format(returnVisitDate) + " ");
			message.append("for a follow up visit and pick up your next supply of medicine.");
			smsController.createSms(sendTo, message.toString(),
					dueDate.getTime(), "FAST", "");
			log.info(message.toString());
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendTreatmentInitiationSms(Encounter encounter, SmsController smsController) {

		Map<String, Object> observation = encounter.getObservations();
		String rtnVisitDate = observation.get("return_visit_date").toString()
				.toUpperCase();
		Patient patient = openmrs.getPatientByIdentifier(encounter.getPatientId());
		if (rtnVisitDate.equals(null)
				|| !openmrs.getfilter(encounter, "FAST-Treatment Followup")
				|| patient.isDead()) {
			return false;
		}
		Date returnVisitDate;
		try {
			String sendTo = encounter.getPatientContact();
			// System.out.println(sendTo);
			returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,
					DateTimeUtil.SQL_DATETIME);
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			System.out.println(encounter.getLocation());
			Map<String, Object> observations = encounter.getObservations();
			Location referralLocation = null;
			String id = openmrs.checkReferelPresent(encounter);
			if (!id.equals("")) {
				Encounter ency = openmrs.getEncounter(Integer.parseInt(id), 28);
				observations = openmrs.getEncounterObservations(ency);
				ency.setObservations(observations);
				String referralSite = observations.get("referral_site")
						.toString();
				referralLocation = openmrs.getLocationByShortCode(referralSite);
			} else {
				referralLocation = openmrs.getLocationByShortCode(encounter.getLocation());
			}
			encounter.setLocation(referralLocation.getName());
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			sdf.applyPattern("EEEE d MMM yyyy");

			/* String df=DateFormat.getDateInstance().format(dueDate.getTime()); */
			StringBuilder message = new StringBuilder();
			message.append("Dear " + encounter.getPatientName() + ", ");
			message.append("please come to "
					+ encounter.getLocation()
					+ " on "
					+ sdf.format(dueDate.getTime())
					+ " "
					+ " for a follow up visit and pick up your next supply of medicine.");

			// sendTo = "03222808980";
			System.out.println(dueDate.getTime());
			sendTo = sendTo.replace("-", "");

			String response = smsController
					.createSms(sendTo, message.toString(), dueDate.getTime(),
							"FAST", rtnVisitDate);
			System.out.println(response);
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

}
