package com.ihsinformatics.gfatmnotifications.Implementer;

import java.util.Calendar;

import com.ihsinformatics.gfatmnotifications.jobs.PatientScheduledEmailNotificationJob;

public class EmailServiceImpl implements NotificationService {

	private Calendar calendar = Calendar.getInstance();
	private PatientScheduledEmailNotificationJob emaiJob;

	@Override
	public void run() {

	/*	//Gfatm_notification execute daily...
		EmailNotificationsJob emailNotificationsJob = new EmailNotificationsJob();
		emailNotificationsJob.execute();*/
		
		//Call Center Email Notification (Bi-week)
		if (conditions()) {
			emaiJob = new PatientScheduledEmailNotificationJob();
			emaiJob.execute();
		}

	}

	public boolean conditions() {
		
		boolean isExecutionDay = false;
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			isExecutionDay = true;
			break;
		case Calendar.THURSDAY:
			isExecutionDay = true;
			break;
		case Calendar.FRIDAY:
			isExecutionDay = true;
			break;
		}

		return isExecutionDay;
	}

}
