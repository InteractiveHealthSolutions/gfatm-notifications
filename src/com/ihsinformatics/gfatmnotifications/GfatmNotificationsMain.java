/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */
package com.ihsinformatics.gfatmnotifications;

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

import com.ihsinformatics.gfatmnotifications.controller.SmsController;
import com.ihsinformatics.gfatmnotifications.job.CallNotificationsJob;
import com.ihsinformatics.gfatmnotifications.job.SmsNotificationsJob;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.service.EmailServiceInjector;
import com.ihsinformatics.gfatmnotifications.service.ConsumerService;
import com.ihsinformatics.gfatmnotifications.service.NotificationInjector;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;

/**
 * @author owais.hussain@ihsinformatics.com
 * @author Shujaat
 *
 */

public class GfatmNotificationsMain {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private Scheduler smsScheduler;
	private Scheduler callScheduler;
	private static NotificationInjector injector;
	private static ConsumerService consumer;

	/**
	 * @param args
	 * @throws InputRequiredException
	 * @throws DatabaseUpdateException
	 * @throws ModuleMustStartException
	 */
	public static void main(String[] args) {
		try {
			// Email Notification
			injector = new EmailServiceInjector();
			consumer = injector.getConsumer();
			consumer.getConnection(Constants.WAREHOUSE_CONNECTION);
			consumer.process();
			log.info("Email Notification execution is complete on : " + new Date());

			// SMS Notification injector = new SmsServiceInjector(); consumer
			/*
			 * injector = new SmsServiceInjector(); consumer = injector.getConsumer();
			 * consumer.getConnection(Constants.OPENMRS_CONNECTION); consumer.process();
			 */

			System.exit(0);

		} catch (Exception e) {
			log.info("Exception : " + e.getMessage());
			System.exit(-1); // this will be remove
		}
	}

	public GfatmNotificationsMain() {

		DatabaseConnection connection = new DatabaseConnection();
		/*
		 * if (!connection.openmrsDbConnection()) {
		 * System.out.println("Failed to connect with local database. Exiting"); }
		 */
		if (!connection.wareHouseConnection()) {
			System.out.println("Failed to connect with warehouse local database. Exiting");
			System.exit(-1);
		}

	}

	public void createSmsJob() throws SchedulerException {
		DateTime from = new DateTime();
		from.minusHours(Constants.SMS_SCHEDULE_INTERVAL_IN_HOURS);
		DateTime to = new DateTime();
		smsScheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail smsJob = JobBuilder.newJob(SmsNotificationsJob.class).withIdentity("smsJob", "smsGroup").build();
		SmsNotificationsJob smsJobObj = new SmsNotificationsJob();
		smsJobObj.setLocalDb(UtilityCollection.getInstance().getLocalDb());
		smsJobObj.setOpenmrs(new OpenMrsUtil(UtilityCollection.getInstance().getLocalDb()));
		smsJobObj.setDateFrom(from);
		smsJobObj.setDateTo(to);
		smsJobObj.setSmsController(
				new SmsController(Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY, Constants.SMS_USE_SSL));
		smsJob.getJobDataMap().put("smsJob", smsJobObj);

		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMinutes(Constants.SMS_SCHEDULE_INTERVAL_IN_HOURS).repeatForever();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("smsTrigger", "smsGroup")
				.withSchedule(scheduleBuilder).build();
		smsScheduler.scheduleJob(smsJob, trigger);
		smsScheduler.start();
	}

	public void createCallJob() throws SchedulerException {
		DateTime from = new DateTime();
		from.minusHours(Constants.CALL_SCHEDULE_INTERVAL_IN_HOURS);
		callScheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail callJob = JobBuilder.newJob(CallNotificationsJob.class).withIdentity("callJob", "callGroup").build();
		/*
		 * CallNotificationsJob callJobObj = new CallNotificationsJob();
		 * callJobObj.setLocalDb(localDb); callJobObj.setDateFrom(from);
		 * callJobObj.setDateTo(to); callJob.getJobDataMap().put("callJob", callJobObj);
		 */

		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInHours(Constants.CALL_SCHEDULE_INTERVAL_IN_HOURS);

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("callTrigger", "notificationsGroup")
				.withSchedule(scheduleBuilder).build();

		callScheduler.scheduleJob(callJob, trigger);
		callScheduler.start();
	}

	public void createEmailJob() {

	}
}
