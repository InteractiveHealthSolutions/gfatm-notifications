package com.ihsinformatics.gfatmnotifications.email.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;

import com.ihsinformatics.emailer.EmailEngine;
import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Contact;
import com.ihsinformatics.gfatmnotifications.common.model.PatientScheduled;
import com.ihsinformatics.gfatmnotifications.email.service.EmailService;
import com.ihsinformatics.gfatmnotifications.email.util.CustomGfatmDatabaseUtil;
import com.ihsinformatics.gfatmnotifications.email.util.HtmlUtil;
import com.ihsinformatics.util.DatabaseUtil;

public class CallCenterEmailJob implements EmailService {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private List<PatientScheduled> scheduledPatientList;
	private CustomGfatmDatabaseUtil openMrsUtil;
	private Properties props;
	private String subject, subjectNotFound, watcherEmail, from, bodyMessage, developerEmailAddress;
	private Set<String> locationsSet;
	private String noUpdateAvailableSubject, ccConcernPerson;
	private DatabaseUtil dbUtils;

	@Override
	public void initializeProperties() {
		scheduledPatientList = new ArrayList<PatientScheduled>();
		locationsSet = new HashSet<String>();
		props = Context.getProps();
		watcherEmail = props.getProperty("emailer.watcher.email.address");
		subject = props.getProperty("mail.patient.schedule.subject");
		subjectNotFound = props.getProperty("mail.location.subject");
		noUpdateAvailableSubject = props.getProperty("mail.update.available");
		from = props.getProperty("mail.user.username");
		ccConcernPerson = props.getProperty("callcenter.concern.person.name");
		bodyMessage = props.getProperty("mail.body.message");
		developerEmailAddress = props.getProperty("developer.email.address");
		dbUtils = Context.getDwDb(); //
	}

	@Override
	public void execute(CustomGfatmDatabaseUtil openMrsUtil) {
		this.openMrsUtil = openMrsUtil;
		scheduledPatient();
		log.info("Call Center Patient Schedule is successfully executed...");
	}

	protected void scheduledPatient() {
		Contact supervisorEmail;
		String htmlConvertStr;
		// First Step1
		scheduledPatientList = openMrsUtil.getPatientScheduledList();
		if (!scheduledPatientList.isEmpty()) {
			Iterator<PatientScheduled> iterator = scheduledPatientList.iterator();
			while (iterator.hasNext()) {
				PatientScheduled patientScheduled = iterator.next();
				if (StringUtils.isBlank(HtmlUtil.getInstance().missedFupConditions(patientScheduled))) {
					supervisorEmail = Context.getUserContactByLocationName(patientScheduled.getFacilityScheduled(),dbUtils);
				} else {
					supervisorEmail = Context.getUserContactByLocationName(patientScheduled.getFacilityName(),dbUtils);
				}

				if (supervisorEmail == null) {
					locationsSet.add(patientScheduled.getFacilityScheduled());
					List<PatientScheduled> removeLocation = new ArrayList<>();
					if (patientScheduled.getMvfReturnVisitDate() != null) {

						removeLocation = openMrsUtil
								.getPatientByScheduledFacilityName(patientScheduled.getFacilityName());
					} else {
						if (patientScheduled.getFacilityScheduled() != null) {

							removeLocation = openMrsUtil
									.getPatientByScheduledFacilityName(patientScheduled.getFacilityScheduled());
						}
					}
					scheduledPatientList.removeAll(removeLocation);// remove list of same location.
					if (scheduledPatientList.size() > 1) {
						iterator = scheduledPatientList.iterator();
					} else {
						System.out.println("Length : " + scheduledPatientList.size());
						break;
					}

				} else {
					List<PatientScheduled> patientScheduledResult = new ArrayList<>();
					if (patientScheduled.getMvfReturnVisitDate() != null) {
						patientScheduledResult = openMrsUtil
								.getPatientByScheduledFacilityName(patientScheduled.getFacilityName());
					} else {
						if (patientScheduled.getFacilityScheduled() != null) {
							patientScheduledResult = openMrsUtil
									.getPatientByScheduledFacilityName(patientScheduled.getFacilityScheduled());
						}
					}
					scheduledPatientList.removeAll(patientScheduledResult);
					iterator = scheduledPatientList.iterator();// new Iterator.
					htmlConvertStr = HtmlUtil.getInstance().getHtmlTableWithMultipleCol(patientScheduledResult);

					sendEmail(supervisorEmail.getEmailAdress(), htmlConvertStr, subject);
					sendEmail(watcherEmail, htmlConvertStr, subject);
					sendEmail("kareem.shaikh@ihsinformatics.com", htmlConvertStr, subject);
					// sendEmail("shujaat.ali@ihsinformatics.com", htmlConvertStr, subject);
				}
			}
			if (!locationsSet.isEmpty() && locationsSet != null) {
				sendEmail(watcherEmail, HtmlUtil.getInstance().getHtmlTableWithSet(locationsSet), subjectNotFound);
			}
		} else {
			sendEmail(watcherEmail,
					HtmlUtil.getInstance().getMessageFormat(ccConcernPerson, bodyMessage, "Call Center"),
					noUpdateAvailableSubject);
		}
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
