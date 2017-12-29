/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */
package com.ihsinformatics.gfatmnotifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

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
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ihsinformatics.gfatmnotifications.controllers.EmailController;
import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.databaseconnections.Connections;
import com.ihsinformatics.gfatmnotifications.jobs.CallNotificationsJob;
import com.ihsinformatics.gfatmnotifications.jobs.EmailNotificationsJob;
import com.ihsinformatics.gfatmnotifications.jobs.SmsNotificationsJob;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.FastFact;
import com.ihsinformatics.gfatmnotifications.model.UtilityCollection;
import com.ihsinformatics.gfatmnotifications.ui.SwingControl;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.mysql.jdbc.Connection;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
@SuppressWarnings("deprecation")
public class GfatmNotificationsMain {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static final String BASE_URL = "http://124.29.207.74:9902/openmrs/ws/rest/v1";
	private Scheduler smsScheduler;
	private Scheduler callScheduler;
	private Scheduler emailScheduler;
	/**
	 * @param args
	 * @throws InputRequiredException
	 * @throws DatabaseUpdateException
	 * @throws ModuleMustStartException
	 */
	public static void main(String[] args) {
	
		// Notifications part
		GfatmNotificationsMain gfatm = new GfatmNotificationsMain();
		 SwingControl swingControlDemo = new SwingControl();
		 swingControlDemo.showLabelDemo();
		try {
			//gfatm.createSmsJob();
			//gfatm.createCallJob();
			  gfatm.createEmailJob();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Executes HTTP GET request and returns the response

	 * @param restPart
	 * @param echo
	 * @return this method is design for openmrs restful api... current not in use 
	 * may be  in future we have to move on restful api 
	 * @throws AuthenticationException
	 * @throws ClientProtocolException
	 */
	public static String get(String query, String username, String password)
			throws AuthenticationException, ClientProtocolException,
			IOException {
		String response = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			String URL = BASE_URL + query;
			HttpGet httpGet = new HttpGet(URL);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					username, password);
			BasicScheme scheme = new BasicScheme();
			Header authorizationHeader = scheme.authenticate(credentials,
					httpGet);
			httpGet.setHeader(authorizationHeader);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httpGet, responseHandler);
		} finally {
			httpClient.getConnectionManager().shutdown();
			httpClient.close();
		}
		return response;
	}

	public GfatmNotificationsMain() {
		
		Connections connection = new Connections();
		if (!connection.openmrsDbConnection()) {
			System.out.println("Failed to connect with local database. Exiting");
		}
		if (!connection.wareHouseConnection()) {
			System.out.println("Failed to connect with warehouse local database. Exiting");
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
		DateTime to = new DateTime();
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

	public void createEmailJob() throws SchedulerException {
		
		emailScheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail emailJob = JobBuilder.newJob(EmailNotificationsJob.class)
				.withIdentity("emailJob", "emailGroup").build();
		
		EmailNotificationsJob emailJobObj = new EmailNotificationsJob();
		emailJobObj.setLocalDb(UtilityCollection.getWarehouseDb());
		emailJobObj.setOpenmrsWarehouse(new OpenMrsUtil(UtilityCollection.getWarehouseDb()));
		emailJobObj.setProps(Connections.prop);
		emailJobObj.setEmailController(new EmailController());
		emailJob.getJobDataMap().put("emailJob", emailJobObj);
        System.out.println(""+UtilityCollection.hours +" "+ UtilityCollection.minutes);
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("emailTrigger", "notificationsGroup")
				.withSchedule(CronScheduleBuilder.cronSchedule(""+UtilityCollection.seconds+" "+UtilityCollection.minutes+" "+UtilityCollection.hours+" * * ?")).build(); //trigger will fire daily at 15:46 pm:
		emailScheduler.scheduleJob(emailJob, trigger);
		emailScheduler.start();
	}
}
