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
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ihsinformatics.gfatmnotifications.ui.SwingControl;
import com.ihsinformatics.util.DatabaseUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
@SuppressWarnings("deprecation")
public class GfatmNotificationsMain {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static final String BASE_URL = "http://124.29.207.74:9902/openmrs/ws/rest/v1";
	private static final String userHome = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "gfatm";
	private static String propFilePath = userHome
			+ System.getProperty("file.separator")
			+ "gfatm-notifications.properties";
	private static Properties props;
	private static String title = "GFATM Notifications ";
	private static DatabaseUtil localDb;
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
			gfatm.createSmsJob();
			gfatm.createCallJob();
			gfatm.createEmailJob();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Executes HTTP GET request and returns the response
	 * 
	 * @param restPart
	 *            only the part after BASE_URL is required here, e.g.
	 *            /rest/countries
	 * @param echo
	 * @return
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
		System.out.println("*** Starting up " + title + " ***");
		System.out.println("Reading properties...");
		System.out.println(propFilePath);
		readProperties(propFilePath);
		String url = props.getProperty("local.connection.url");
		String driverName = props.getProperty("local.connection.driver_class");
		String dbName = props.getProperty("local.connection.database");
		String userName = props.getProperty("local.connection.username");
		String password = props.getProperty("local.connection.password");
		System.out.println(url + " " + dbName + " " + driverName + " "
				+ userName + " " + password);
		localDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		if (!localDb.tryConnection()) {
			System.out
					.println("Failed to connect with local database. Exiting");
			System.exit(-1);
		}
	}

	/**
	 * Read properties from properties file
	 */
	public void readProperties(String propertiesFile) {
		InputStream propFile;
		try {
			if (!(new File(userHome).exists())) {
				boolean checkDir = new File(userHome).mkdir();
				if (!checkDir) {
					JOptionPane
							.showMessageDialog(
									null,
									"Could not create properties file. Please check the permissions of your home folder.",
									"Error!", JOptionPane.ERROR_MESSAGE);
				}
			}
			propFile = new FileInputStream(propertiesFile);
			if (propFile != null) {
				props = new Properties();
				props.load(propFile);
				title += props.getProperty("app.version");
			}
		} catch (FileNotFoundException e1) {
			log.severe("Properties file not found or is inaccessible.");
		} catch (IOException e) {
			e.printStackTrace();
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
		smsJobObj.setLocalDb(localDb);
		smsJobObj.setOpenmrs(new OpenMrsUtil(localDb));
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
		DateTime from = new DateTime();
		from.minusHours(Constants.EMAIL_SCHEDULE_INTERVAL_IN_HOURS);
		DateTime to = new DateTime();
		emailScheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail emailJob = JobBuilder.newJob(EmailNotificationsJob.class)
				.withIdentity("emailJob", "emailGroup").build();
		EmailNotificationsJob emailJobObj = new EmailNotificationsJob();
		emailJobObj.setLocalDb(localDb);
		emailJobObj.setDateFrom(from);
		emailJobObj.setDateTo(to);
		emailJob.getJobDataMap().put("emailJob", emailJobObj);
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
				.simpleSchedule().withIntervalInHours(
						Constants.EMAIL_SCHEDULE_INTERVAL_IN_HOURS);
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("emailTrigger", "notificationsGroup")
				.withSchedule(scheduleBuilder).build();
		emailScheduler.scheduleJob(emailJob, trigger);
		emailScheduler.start();
	}
}
