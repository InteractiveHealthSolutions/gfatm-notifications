/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */
package com.ihsinformatics.gfatmnotifications;

import java.io.IOException;
import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ihsinformatics.gfatmnotifications.Implementer.EmailManager;
import com.ihsinformatics.gfatmnotifications.Interface.IConsumer;
import com.ihsinformatics.gfatmnotifications.Interface.Iemail;
import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.databaseconnections.Connections;
import com.ihsinformatics.gfatmnotifications.jobs.CallNotificationsJob;
import com.ihsinformatics.gfatmnotifications.jobs.EmailNotificationsJob;
import com.ihsinformatics.gfatmnotifications.jobs.PatientScheduledEmailNotificationJob;
import com.ihsinformatics.gfatmnotifications.jobs.SmsNotificationsJob;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.service.EmailServiceInjector;
import com.ihsinformatics.gfatmnotifications.service.NotificationInjector;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;

/**
 * @author owais.hussain@ihsinformatics.com 
 *
 */
@SuppressWarnings("deprecation")
public class GfatmNotificationsMain {

	private Scheduler smsScheduler;
	private Scheduler callScheduler;
	/**
	 * @param args
	 * @throws InputRequiredException
	 * @throws DatabaseUpdateException
	 * @throws ModuleMustStartException
	 */
	public static void main(String[] args) {
	
		// Notifications part
		GfatmNotificationsMain gfatm = new GfatmNotificationsMain();
		 /*SwingControl swingControlDemo = new SwingControl();
		 swingControlDemo.showLabelDemo();*/
		try {
			
		/*	//Send email
			NotificationInjector injector = new EmailServiceInjector();
			IConsumer consumer = injector.getConsumer();
			consumer.getConnection(Constants.WAREHOUSE_CONNECTION);//Here we can pass string or required database connection e.g warehosue and  openmrs database connecion etc .
			consumer.process();*/
			
			 // gfatm.createSmsJob();
			 //gfatm.createCallJob();
			   gfatm.createEmailJob();
			   System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public GfatmNotificationsMain() {
		
		Connections connection = new Connections();
		/*if (!connection.openmrsDbConnection()) {
			System.out.println("Failed to connect with local database. Exiting");	
		}*/
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
		JobDetail smsJob = JobBuilder.newJob(SmsNotificationsJob.class)
				.withIdentity("smsJob", "smsGroup").build();
		SmsNotificationsJob smsJobObj = new SmsNotificationsJob();
		smsJobObj.setLocalDb(UtilityCollection.getLocalDb());
		smsJobObj.setOpenmrs(new OpenMrsUtil(UtilityCollection.getLocalDb()));
		smsJobObj.setDateFrom(from);
		smsJobObj.setDateTo(to);
		smsJobObj.setSmsController(new SmsController(
				Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
				Constants.SMS_USE_SSL));
		smsJob.getJobDataMap().put("smsJob", smsJobObj);
		
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
				.simpleSchedule()
				.withIntervalInMinutes(Constants.SMS_SCHEDULE_INTERVAL_IN_HOURS)
				.repeatForever();
		
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("smsTrigger", "smsGroup")
				.withSchedule(scheduleBuilder).build();
		smsScheduler.scheduleJob(smsJob, trigger);
		smsScheduler.start();
	}

	public void createCallJob() throws SchedulerException {
		DateTime from = new DateTime();
		from.minusHours(Constants.CALL_SCHEDULE_INTERVAL_IN_HOURS);
		callScheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail callJob = JobBuilder.newJob(CallNotificationsJob.class)
				.withIdentity("callJob", "callGroup").build();
		/*
		 * CallNotificationsJob callJobObj = new CallNotificationsJob();
		 * callJobObj.setLocalDb(localDb); callJobObj.setDateFrom(from);
		 * callJobObj.setDateTo(to); callJob.getJobDataMap().put("callJob",
		 * callJobObj);
		 */

		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
				.simpleSchedule().withIntervalInHours(
						Constants.CALL_SCHEDULE_INTERVAL_IN_HOURS);

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("callTrigger", "notificationsGroup")
				.withSchedule(scheduleBuilder).build();

		callScheduler.scheduleJob(callJob, trigger);
		callScheduler.start();
	}

	public void createEmailJob() {
		
		Iemail emailInterface = new EmailManager();
		emailInterface.execute();
	}
}
