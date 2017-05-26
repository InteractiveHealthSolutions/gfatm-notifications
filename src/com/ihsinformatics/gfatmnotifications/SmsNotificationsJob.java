/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.model.Encounter;
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
		initialize(smsJob);
		dateFrom = dateFrom.withMonthOfYear(3);
		executeFastSms(dateFrom, dateTo);
	}

	private void executeFastSms(DateTime dateFrom, DateTime dateTo) {
		int type = 0;
		// Get all encounter
		List<Encounter> encounters = getOpenmrs().getEncounters(dateFrom,
				dateTo, type);
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
				sendGeneXpertSms(encounter);
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
				sendReferralFormSms(encounter);
				break;
			case "FAST-Screening":
				break;
			case "FAST-Screening CXR Test Order":
				break;
			case "FAST-Screening CXR Test Result":
				break;
			case "FAST-Treatment Followup":
				sendTreatmentFollowupSms(encounter);
				break;
			case "FAST-Treatment Initiation":
				sendTreatmentInitiationSms(encounter);
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

	private void sendReferralFormSms(Encounter encounter) {
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

	private void sendTreatmentFollowupSms(Encounter encounter) {
		Map<String, Object> observations = encounter.getObservations();
		String returnVisitStr = observations.get("return_visit_date")
				.toString().toUpperCase();
		Date returnVisitDate;
		try {
			returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,
					DateTimeUtil.SQL_DATETIME);
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			String sendTo = encounter.getPatientContact();
			StringBuilder message = new StringBuilder();
			message.append("Dear " + encounter.getPatientName() + ", ");
			message.append("please come to " + encounter.getLocation() + " on ");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			message.append(sdf.format(returnVisitDate) + " ");
			message.append("for a follow up visit and pick up your next supply of medicine.");
			smsController.createSms(sendTo, message.toString(),
					dueDate.getTime(), "FAST", "");
			log.info(message.toString());
		} catch (ParseException e) {
			log.warning(e.getMessage());
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
	}

	private void sendTreatmentInitiationSms(Encounter encounter) {
		StringBuilder message = new StringBuilder();
		Map<String, Object> observations = encounter.getObservations();
		String gxpResult = observations.get("gxp_result").toString()
				.toUpperCase();
		message.append("Dear " + encounter.getPatientName() + ", ");
		message.append("your test result is ready for collection at "
				+ encounter.getLocation() + ". ");
		message.append("Please collect at your earliest convenience.");
		try {
			smsController.createSms(encounter.getPatientContact(),
					message.toString(), new Date(), "FAST", gxpResult);
			log.info(message.toString());
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
	}
}
