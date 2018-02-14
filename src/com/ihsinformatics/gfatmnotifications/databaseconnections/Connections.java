package com.ihsinformatics.gfatmnotifications.databaseconnections;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.ihs.emailer.EmailEngine;
import org.ihs.emailer.EmailException;

import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;
import com.ihsinformatics.util.DatabaseUtil;

public class Connections {

	/**
	 * This class is under construction -> decor designe is working on this
	 * class
	 */
	private static final Logger	log				= Logger.getLogger(Class.class
														.getName());

	private static final String	userHome		= System.getProperty("user.home")
														+ System.getProperty("file.separator")
														+ "gfatm";
	private static String		propFilePath	= userHome
														+ System.getProperty("file.separator")
														+ "gfatm-notifications.properties";
	private static Properties	props;
	private static String		title			= "GFATM Notifications ";
	private static DatabaseUtil	localDb, whLocalDb;
	public static Properties	prop;
	public static String		guestUsername	= "";
	public static String		guestPassword	= "";

	public Connections() {
		readProperties(propFilePath);
	}

	/**
	 * Openmrs Database connections
	 *
	 * @return
	 */
	public boolean openmrsDbConnection() {

		if (localDb != null) {
			return false;
		}
		System.out.println("*** Starting up " + title + " ***");
		System.out.println("Reading properties...");
		System.out.println(propFilePath);
		String url = props.getProperty("local.connection.url");
		String driverName = props.getProperty("local.connection.driver_class");
		String dbName = props.getProperty("local.connection.database");
		String userName = props.getProperty("local.connection.username");
		String password = props.getProperty("local.connection.password");
		System.out.println(url + " " + dbName + " " + driverName + " "
				+ userName + " " + password);
		localDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		if (!localDb.tryConnection()) {
			return false;
			// System.exit(-1);
		}

		UtilityCollection.setLocalDb(localDb);
		return true;

	}

	/**
	 * warehouse database connections
	 *
	 * @return
	 */
	public boolean wareHouseConnection() {

		System.out.println("*** Starting up " + title + " ***");
		System.out.println("Reading properties...");
		// get connection of data ware house
		String whUrl = props.getProperty("local.wh.connection.url");
		String whDriverName = props
				.getProperty("local.wh.connection.driver_class");
		String whDbName = props.getProperty("local.wh.connection.database");
		String whUserName = props.getProperty("local.wh.connection.username");
		String whPassword = props.getProperty("local.wh.connection.password");
		whLocalDb = new DatabaseUtil(whUrl, whDbName, whDriverName, whUserName,
				whPassword);
		if (!whLocalDb.tryConnection()) {
			System.out.println("*** Warehouse database is not connected ***");
			return false;
		}
		UtilityCollection.setWarehouseDb(whLocalDb);
		// System.out.println("*** Starting Email Engine ***");
		return startEmailEngine();

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

	/**
	 * Start Email Engine instance
	 */
	public boolean startEmailEngine() {
		boolean isEnginStart = true;
		try {
			InputStream inputStream = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(Constants.PROP_FILE_NAME);
			// InputStream inputStream =
			// Connections.class.getResourceAsStream(Constants.PROP_FILE_NAME);
			prop = new Properties();
			prop.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			log.warning("InpuStream In Email Engine method : " + e.getMessage());
			isEnginStart = false;
		}
		guestUsername = prop.getProperty("mail.user.username");
		guestPassword = prop.getProperty("mail.user.password");
		UtilityCollection.minutes = prop.getProperty("mail.notifiation.minute");
		UtilityCollection.seconds = prop
				.getProperty("mail.notification.second");
		UtilityCollection.hours = prop.getProperty("mail.notification.hour");
		UtilityCollection.setProps(prop);
		try {
			System.out.println("*** Values **" + prop.isEmpty());
			System.out.println("*** Starting Email Engine ***");
			EmailEngine.instantiateEmailEngine(prop);
			isEnginStart = true;

		} catch (EmailException e) {
			e.printStackTrace();
			log.warning("Email Engine Exception : " + e.getMessage());
			isEnginStart = false;
		}

		return isEnginStart;

	}

}
