package com.ihsinformatics.gfatmnotifications.programs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.Interface.Isms;
import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.util.DateTimeUtil;

public class Fast implements Isms {
	private static final Logger	log	= Logger.getLogger(Class.class.getName());
	private OpenMrsUtil openmrsUtil;
	private SmsController smsController;
	public final SimpleDateFormat	DATE_FORMAT	= new SimpleDateFormat("dd-MMM-yyyy");
	@Override
	public void initializeProperties(OpenMrsUtil openMrsUtil,SmsController smsController) {

		this.openmrsUtil = openMrsUtil;
		this.smsController =smsController;
	}
	 
	@Override
	public void execute(List<Encounter> encounters) {
		
			for (Encounter encounter : encounters) {
						Map<String, Object> observations =  openmrsUtil.getEncounterObservations(encounter);;
						encounter.setObservations(observations);
						System.out.println("Observation : "+ encounter.getEncounterType());
						switch (encounter.getEncounterType()) {
								case "FAST-GXP Test":
									// sendGeneXpertSms(encounter);
									break;
								case "FAST-Referral Form":
								    sendReferralForm(encounter);
									break;
								case "FAST-Treatment Followup":
									//sendTreatmentFollowup(encounter);
									break;
								case "FAST-Treatment Initiation":
									 //sendTreatmentInitiation(encounter);
									break;
								default:
						}
				}
	}

	public boolean sendReferralForm(Encounter encounter) {
		
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
			    Location referralLocation = openmrsUtil.getLocationByShortCode(referralSite);
			
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
	public boolean sendTreatmentInitiation(Encounter encounter) {

		Date returnVisitDate = new Date(); //this  initialization need to be review ...
		Map<String, Object> observation = encounter.getObservations();
		System.out.print(observation.toString());
		String rtnVisitDate = observation.get("return_visit_date").toString()
				.toUpperCase();
		String isTbPatient = observation.get("tb_patient").toString()
				.toUpperCase();
		String antibiotic = "";
		Patient patient = openmrsUtil.getPatientByIdentifier(encounter
				.getIdentifier());

		// we need FAST end of follow up form data.
		Map<String, Object> EndOfFollowUpObservation = null;
		Encounter encounterEndFup = openmrsUtil.getEncounterByPatientIdentifier(
				encounter.getIdentifier(),
				Constants.END_FOLLOWUP_FORM_ENCOUNTER_TYPE);
		Map<String, Object> observations = openmrsUtil.getEncounterObservations(encounter);;
		encounterEndFup.setObservations(observations);
		EndOfFollowUpObservation = encounterEndFup.getObservations();
		try {
			 returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,DateTimeUtil.SQL_DATETIME);
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
		if (!EndOfFollowUpObservation.get("treatment_outcome").equals("ANTIBIOTIC COMPLETE - NO TB")) {
			 	return false;
		}
		// we need to check the treatment_outcome from end of follow of form
		if (rtnVisitDate.equals(null) || patient.isDead()) {
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
			String id = openmrsUtil.checkReferelPresent(encounter);
			if (!id.equals("")) {

				Encounter ency = openmrsUtil.getEncounter(Integer.parseInt(id),
						Constants.REFERREL_ENCOUNTER_TYPE);
				observation = openmrsUtil.getEncounterObservations(ency);
				ency.setObservations(observation);
				String referralSite = observation.get("referral_site")
						.toString();
				referralLocation = openmrsUtil.getLocationByShortCode(referralSite);
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

	@SuppressWarnings("deprecation")
	public boolean sendTreatmentFollowup(Encounter encounter) {
		Date returnVisitDate = new Date();
		Map<String, Object> observations = encounter.getObservations();
		String returnVisitStr = observations.get("return_visit_date").toString().toUpperCase();
		
		/************ Conditions ***************/
		if (returnVisitStr.equals(null)|| !openmrsUtil.isTransferOrReferel(encounter)) {
			 return false;
		}
		try {
				// Past date is not allowed so, we ignore or skip the past date
				// notifications
				returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,DateTimeUtil.SQL_DATETIME);
				Date currentDate = new SimpleDateFormat("dd-MMM-yyyy")
				.parse(DATE_FORMAT.format(new Date()));
	
				if (returnVisitDate.before(currentDate)) {
					return false;
				}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String id = openmrsUtil.checkReferelPresent(encounter);
		Location referralLocation = null;

		if (!id.equals("")) {

			Encounter ency = openmrsUtil.getEncounter(Integer.parseInt(id), 28);
			observations = openmrsUtil.getEncounterObservations(ency);
			ency.setObservations(observations);
			String referralSite = observations.get("referral_site").toString();
			referralLocation = openmrsUtil.getLocationByShortCode(referralSite);
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
					message.append("" + encounter.getLocation()+ " pe ap ko doctor ke paas ");
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


}
