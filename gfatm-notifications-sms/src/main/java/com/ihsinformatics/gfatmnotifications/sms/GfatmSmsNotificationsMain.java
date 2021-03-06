/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */
package com.ihsinformatics.gfatmnotifications.sms;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ihsinformatics.gfatmnotifications.sms.service.AbstractSmsNotificationsJob;
import com.ihsinformatics.gfatmnotifications.sms.service.ReminderSmsNotificationsJob;
import com.ihsinformatics.gfatmnotifications.sms.service.SmsNotificationsJob;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */

public class GfatmSmsNotificationsMain {

	// Detect whether the app is running in DEBUG mode or not
	public static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
			.indexOf("-agentlib:jdwp") > 0;
	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static Scheduler scheduler;

	/**
	 * @param args
	 * @throws InputRequiredException
	 * @throws DatabaseUpdateException
	 * @throws ModuleMustStartException
	 */
	public static void main(String[] args) {
		try {
			boolean runAlerts = false;
			boolean runReminders = false;
			// Check arguments first
			if (args[0] == null || args.length == 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Arguments are invalid. Arguments must be provided as:\r\n").append("-a to run alerts")
						.append("\r\n").append("-r to run reminders").append("\r\n")
						.append("-p to define path to properties file.").append("\r\n").append("-i to run immediately")
						.append("\r\n")
						.append("Both -a and -r should not be set at once, because only one of them will execute.");
				System.out.println(sb.toString());
				System.exit(0);
			}
			// Read arguments
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-a")) {
					runAlerts = true;
				} else if (args[i].equals("-r")) {
					runReminders = true;
				} else if (args[i].equals("-p")) {
					// Set path
				} else if (args[i].equals("-i")) {
					SmsContext.SMS_SCHEDULE_START_TIME = new Date(new Date().getTime() + 300);
				}
			}
			GfatmSmsNotificationsMain sms = new GfatmSmsNotificationsMain();
			scheduler = StdSchedulerFactory.getDefaultScheduler();

			if (runAlerts) {
				// Create SMS Job
				AbstractSmsNotificationsJob smsAlertJobObj = new SmsNotificationsJob();
				smsAlertJobObj.setDateFrom(new DateTime().minusHours(SmsContext.SMS_ALERT_SCHEDULE_INTERVAL_IN_HOURS));
				smsAlertJobObj.setDateTo(new DateTime());
				sms.createJob(smsAlertJobObj, "smsGroup", "smsAlertJob", "smsAlertTrigger",
						SmsContext.SMS_ALERT_SCHEDULE_INTERVAL_IN_HOURS);
			} else if (runReminders) {
				AbstractSmsNotificationsJob smsReminderJobObj = new ReminderSmsNotificationsJob();
				smsReminderJobObj
						.setDateFrom(new DateTime().minusHours(SmsContext.SMS_REMINDER_SCHEDULE_INTERVAL_IN_HOURS));
				smsReminderJobObj.setDateTo(new DateTime());
				sms.createJob(smsReminderJobObj, "smsGroup", "smsReminderJob", "smsReminderTrigger",
						SmsContext.SMS_REMINDER_SCHEDULE_INTERVAL_IN_HOURS);
			}
			// Execute scheduler
			scheduler.start();
		} catch (SchedulerException e) {
			log.severe(e.getMessage());
			System.exit(-1);
		}
	}

	public void createJob(AbstractSmsNotificationsJob jobObj, String groupName, String jobName, String triggerName,
			int repeatIntervalInHours) throws SchedulerException {
		JobDetail job = JobBuilder.newJob(jobObj.getClass()).withIdentity(jobName, groupName).build();
		job.getJobDataMap().put(jobName, jobObj);
		// Create trigger with given interval and start time
		SimpleScheduleBuilder alertScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInHours(repeatIntervalInHours).repeatForever();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, groupName)
				.withSchedule(alertScheduleBuilder).startAt(SmsContext.SMS_SCHEDULE_START_TIME).build();
		scheduler.scheduleJob(job, trigger);
	}
}
