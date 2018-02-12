package com.ihsinformatics.gfatmnotifications.Implementer;

import java.util.Calendar;

import com.ihsinformatics.gfatmnotifications.Interface.Iemail;
import com.ihsinformatics.gfatmnotifications.jobs.EmailNotificationsJob;
import com.ihsinformatics.gfatmnotifications.jobs.PatientScheduledEmailNotificationJob;

public class EmailManager  implements Iemail{
	private Calendar calendar = Calendar.getInstance();
	private PatientScheduledEmailNotificationJob emaiJob;
  
	@Override
	public void execute() {
   	  
		/*EmailNotificationsJob emailNotificationsJob = new EmailNotificationsJob();
		emailNotificationsJob.execute();*/
		
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
	    case Calendar.MONDAY:
	    	emaiJob = new PatientScheduledEmailNotificationJob();
			emaiJob.execute();
			break;
	    case Calendar.TUESDAY:
	    	 emaiJob = new PatientScheduledEmailNotificationJob();
			 emaiJob.execute();
			break;	
	    case Calendar.WEDNESDAY:
	    	 emaiJob = new PatientScheduledEmailNotificationJob();
			 emaiJob.execute();
			break;	
	    case Calendar.THURSDAY:
	    	 emaiJob = new PatientScheduledEmailNotificationJob();
			 emaiJob.execute();
			break;
	    case Calendar.FRIDAY:
	    	 emaiJob = new PatientScheduledEmailNotificationJob();
			 emaiJob.execute();
			break;	
	   }
	
 }

}
