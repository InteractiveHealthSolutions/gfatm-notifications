package com.ihsinformatics.gfatmnotifications.jobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.controllers.EmailController;
import com.ihsinformatics.gfatmnotifications.databaseconnections.Connections;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.model.PatientScheduled;
import com.ihsinformatics.gfatmnotifications.util.HtmlUtile;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;

/**
 * @author Shujaat
 * This job execute Bi-week
 */
public class PatientScheduledEmailNotificationJob {
	
  private static final Logger log = Logger.getLogger(Class.class.getName());
  private ArrayList<PatientScheduled> scheduledPatientList;
  private DateTime startVisitDate;
  private DateTime endVisitDate;
  private OpenMrsUtil openMrsUtil;
  private EmailController emailController;
  private Properties props;
  private String subject,subjectNotFound,watcherEmail,from;
  private Set<String> locationsSet;
  private String noUpdateAvailableSubject;
  
  public PatientScheduledEmailNotificationJob() {
      emailController = new EmailController();
	  openMrsUtil  = new OpenMrsUtil(UtilityCollection.getWarehouseDb());
	  startVisitDate = new DateTime();
	  endVisitDate = startVisitDate.plusDays(3);
	  scheduledPatientList = new ArrayList<PatientScheduled>();
	  locationsSet = new HashSet<String>();
	  props= Connections.prop;
	  watcherEmail =props.getProperty("emailer.watcher.email.address");
	  subject= props.getProperty("mail.patient.schedule.subject");
	  subjectNotFound = props.getProperty("mail.location.subject");
	  noUpdateAvailableSubject = props.getProperty("mail.update.available");
	  from = props.getProperty("mail.user.username");
  } 

  public void execute(){
	
	   String startVisitDateStr = Constants.DATE_FORMATWH.format(startVisitDate.toDate());
	   String endVisitDateStr = Constants.DATE_FORMATWH.format(endVisitDate.toDate());
	   //load emails.
	   openMrsUtil.LoadAllUsersEmail();
	   //load the three day scheduled patients....
       openMrsUtil.getPatientScheduledForVisit(startVisitDateStr, endVisitDateStr);
       scheduledPatient(); 
       log.info("process successfully Terminate...");
       System.exit(0);
  }

  protected void scheduledPatient() {
	  Email supervisorEmail;
	  String htmlConvertStr;
	  
	  //First Step1 
	  scheduledPatientList = UtilityCollection.getInstance().getPatientScheduledsList();
	  if (!scheduledPatientList.isEmpty()) {
			  Iterator<PatientScheduled> iterator = scheduledPatientList.iterator();
			  while (iterator.hasNext()) { //
				  PatientScheduled patientScheduled = iterator.next(); 
				   ///First we need to check 
				   if(StringUtils.isBlank(HtmlUtile.getInstance().missedFupConditions(patientScheduled)))
				        supervisorEmail  = openMrsUtil.getEmailByLocationName(patientScheduled.getFacilityScheduled());
				   else 
					   supervisorEmail = openMrsUtil.getEmailByLocationName(patientScheduled.getFacilityName());
				   
				  if (supervisorEmail == null) {
					    locationsSet.add(patientScheduled.getFacilityScheduled()); 
					    ArrayList<PatientScheduled> removeLocation =  openMrsUtil.getPatientByScheduledFacilityName(patientScheduled.getFacilityScheduled());
					    scheduledPatientList.removeAll(removeLocation);//remove list of same location.
					    iterator = scheduledPatientList.iterator();//reassign the new list.
				  }
				  else{
					    ArrayList<PatientScheduled> patientScheduledResult =  openMrsUtil.getPatientByScheduledFacilityName(patientScheduled.getFacilityScheduled());
					    scheduledPatientList.removeAll(patientScheduledResult);
					    iterator = scheduledPatientList.iterator();//new Iterator.
					    htmlConvertStr = HtmlUtile.getInstance().getHtmlTableWithMultipleCol(patientScheduledResult);
					    sendEmail(watcherEmail, htmlConvertStr, subject);
					    //sendEmail(supervisorEmail.getEmailAdress(), htmlConvertStr, subject);
					  }     
			  }
        if (!locationsSet.isEmpty())//send thos location which are not linked with sit supervisor email.
		   sendEmail(watcherEmail,HtmlUtile.getInstance().getHtmlTableWithSet(locationsSet),subjectNotFound);
	  }
	  else {
		   sendEmail(watcherEmail,HtmlUtile.getInstance().getMessageFormate(),noUpdateAvailableSubject); 
		}
  }
  
  protected boolean sendEmail(String email ,String message,String subject){
		
		boolean isSent = emailController.sendEmailWithHtml(email,
				subject, message,
				from);
		return isSent;	
	}
  
  

}
