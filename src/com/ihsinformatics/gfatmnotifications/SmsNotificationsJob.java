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
import org.json.JSONObject;
import org.junit.Assert;
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
		initialize(smsJob);
		// TODO: Remove the line below on production
		dateFrom = dateFrom.withMonthOfYear(3);
		System.out.println(dateFrom+" "+dateTo);
		
		executeFastSms(dateFrom, dateTo);
		
		// TODO: executeChildhoodTBSms(dateFrom, dateTo);
		// TODO: executePetSms(dateFrom, dateTo);
		// TODO: executeComorbiditiesSms(dateFrom, dateTo);
		// TODO: executePmdtSms(dateFrom, dateTo);
	}

	private void executeFastSms(DateTime dateFrom, DateTime dateTo)  {
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
			/*Map<String, Object> observations = getOpenmrs()
					.getEncounterObservations(encounter);
			encounter.setObservations(observations);*/
			Map<String, Object> observations = getObservations(encounter);;
			encounter.setObservations(observations);
		//	System.out.println(observations);
			//System.out.println(encounter.getEncounterType());
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
				//sendGeneXpertSms(encounter);
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
				sendTreatmentFollowupSms(encounter,openmrs,smsController);
				break;
			case "FAST-Treatment Initiation":
				//sendTreatmentInitiationSms(encounter,openmrs,smsController);
				break;
			default:
				// Do nothing
			}
		}
	}
	
	public Map<String, Object> getObservations(Encounter encounter){
		
	
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
		//	System.out.println(observations);
			//System.out.println(encounter.getEncounterType());
			switch (encounter.getEncounterType()) {
		
			case "FAST-Treatment Followup":
				//sendTreatmentFollowupSms(encounter);
				break;
			case "Childhood TB-Treatment Initiation":
			//	sendTreatmentInitiationSms(encounter);
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
		Map<String, Object> observations = encounter.getObservations();
		Calendar dueDate = Calendar.getInstance();
		String sendTo = encounter.getPatientContact();
		
		//System.out.println(encounter.getPatientId());
		getOpenmrs().getSiteSupervisorContact(encounter, 161550);
		if (sendTo == null) {
			return;
		}
		
		
		Object referredOrTransferred = observations.get("referral_transfer");
		if (referredOrTransferred.equals("PATIENT REFERRED")
				|| referredOrTransferred.equals("PATIENT TRANSFERRED OUT")) {

			String referralSite = observations.get("referral_site").toString();

			StringBuilder query = new StringBuilder();
			query.append("select pc.value as primary_contact from person_attribute as pc ");
			query.append("select pc.value as primary_contact from person_attribute as pc and pc.person_id = (select person_id from person_attribute where person_attribute_type_id = 7 and value = (select location_id from location where name = '"
					+ referralSite + "'))");

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
	}

	public boolean sendTreatmentFollowupSms(Encounter encounter, OpenMrsUtil open, SmsController smsController) {
		Map<String, Object> observations = encounter.getObservations();
		String returnVisitStr = observations.get("return_visit_date")
				.toString().toUpperCase();
		System.out.println("----------------------");
	
		if(returnVisitStr.equals(null) || !open.getfilter(encounter,"FAST-End of Followup")){			
			return false;	
		}
		
	
		Date returnVisitDate;
		try {
			returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,
					DateTimeUtil.SQL_DATETIME);
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			System.out.println(encounter.getLocation());


			String sendTo = encounter.getPatientContact();
			StringBuilder message = new StringBuilder();
			message.append("Dear " + encounter.getPatientName() + ", ");
			message.append("please come to " + encounter.getLocation() + " on ");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			message.append(sdf.format(returnVisitDate) + " ");
			message.append("for a follow up visit and pick up your next supply of medicine.");
			/*smsController.createSms("03222808980", message.toString(),
					dueDate.getTime(), "FAST", "");*/
			log.info(message.toString());
	
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendTreatmentInitiationSms(Encounter encounter, OpenMrsUtil ope, SmsController smsController) {
		
	
		Map<String, Object> observation = encounter.getObservations();	
		String rtnVisitDate = observation.get("return_visit_date").toString()
				.toUpperCase();
		
		if(rtnVisitDate.equals(null)  || !ope.getfilter(encounter,"FAST-Treatment Followup") ){
			return false;
		}
		Date returnVisitDate;
		try{
			returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,
			DateTimeUtil.SQL_DATETIME);
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			StringBuilder message = new StringBuilder();
			System.out.println(encounter.getLocation());
			message.append("Dear " +encounter.getPatientName() + ", ");
			message.append("please come to " + encounter.getLocation()+ " on "+returnVisitDate+" for a follow up visit and pick up your next supply of medicine.");
			
			/*String response = smsController.createSms("03222808980",
			message.toString(), dueDate.getTime(), "FAST", rtnVisitDate);*/
			//System.out.println(response);
		}
		catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}
	
	public void check(){
		System.out.println("helloWorld");
	}
	
}
