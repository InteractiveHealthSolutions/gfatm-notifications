package com.ihsinformatics.gfatmnotifications;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.util.DatabaseUtil;


public class SmsNotificationJobTest {
	SmsNotificationsJob sms ;
	OpenMrsUtil openMrs;
	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static final String userHome = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "gfatm";
	private static String propFilePath = userHome
			+ System.getProperty("file.separator")
			+ "gfatm-notifications.properties";
	private static Properties props;
	private static String title = "GFATM Notifications ";
	private static DatabaseUtil localDb;
	
	@Before
	public void setUp() throws Exception {
		sms= new SmsNotificationsJob();
		System.out.println("*** Starting up " + title + " ***");
		System.out.println("Reading properties...");
		readProperties(propFilePath);
		String url = props.getProperty("local.connection.url");
		String driverName = props.getProperty("local.connection.driver_class");
		String dbName = props.getProperty("local.connection.database");
		String userName = props.getProperty("local.connection.username");
		String password = props.getProperty("local.connection.password");
		System.out.println(url+" "+dbName+" "+driverName+" "+userName+" "+password);
		localDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		if (!localDb.tryConnection()) {
			System.out
					.println("Failed to connect with local database. Exiting");
			System.exit(-1);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void sendTreatmentInitiationSmsTest() {
	
		try{
			openMrs = new OpenMrsUtil(localDb);
			Encounter enc =openMrs.getEncounter(1128, 29);
			Map<String, Object> observations = openMrs.getEncounterObservations(enc);
			enc.setObservations(observations);
			//System.out.println(enc.getEncounterType());
			SmsController smsController = new SmsController(
					Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
					Constants.SMS_USE_SSL);
			boolean response = sms.sendTreatmentInitiationSms(enc,openMrs,smsController);
			System.out.println(response);
			Assert.assertTrue("Error ", response);		
		}
		catch (Exception e) {
			
			Assert.fail("Exception: " + e.getMessage());
		}

	}
	
	@Test
	public void sendTreatmentFollowupSmsTest() {
		
		try{
				openMrs = new OpenMrsUtil(localDb);
				Encounter enc =openMrs.getEncounter(983, 29);
				Map<String, Object> observations = openMrs.getEncounterObservations(enc);
				enc.setObservations(observations);
				//System.out.println(enc.getEncounterType());
				SmsController smsController = new SmsController(
						Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
						Constants.SMS_USE_SSL);
				boolean response = sms.sendTreatmentFollowupSms(enc,openMrs,smsController);
				System.out.println(response);
				Assert.assertTrue("Error ", response);		
		}
		catch (Exception e) {
			
			Assert.fail("Exception: " + e.getMessage());
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
}
