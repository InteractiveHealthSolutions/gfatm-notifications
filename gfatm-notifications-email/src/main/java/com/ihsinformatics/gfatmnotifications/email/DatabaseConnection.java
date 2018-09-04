package com.ihsinformatics.gfatmnotifications.email;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.ihsinformatics.emailer.EmailEngine;
import com.ihsinformatics.emailer.EmailException;
import com.ihsinformatics.gfatmnotifications.email.model.Constants;
import com.ihsinformatics.gfatmnotifications.email.util.UtilityCollection;
import com.ihsinformatics.util.ClassLoaderUtil;
import com.ihsinformatics.util.DatabaseUtil;

public class DatabaseConnection {

	/**
	 * This class is under construction -> decor designe is working on this class
	 */
	private static final Logger log = Logger.getLogger(Class.class.getName());

	public static Properties props;
	private static String title = "GFATM Notifications";
	private static DatabaseUtil localDb, whLocalDb;
	public static String guestUsername = "";
	public static String guestPassword = "";
	boolean isEnginStart = true;

	public DatabaseConnection() {
		readProperties();
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
		System.out.println("Openmrs Reading properties...");

		String url = props.getProperty("local.connection.url");
		String driverName = props.getProperty("local.connection.driver_class");
		String dbName = props.getProperty("local.connection.database");
		String userName = props.getProperty("local.connection.username");
		String password = props.getProperty("local.connection.password");
		System.out.println(url + " " + dbName + " " + driverName + " " + userName + " " + password);
		localDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		if (!localDb.tryConnection()) {
			return false;
		}
		UtilityCollection.getInstance().setLocalDb(localDb);
		return true;

	}

	/**
	 * warehouse database connections
	 *
	 * @return
	 */
	public boolean wareHouseConnection() {

		System.out.println("*** Starting up " + title + " ***");
		System.out.println("Warehouse Reading properties...");
		// get connection of data ware house
		String whUrl = props.getProperty("local.wh.connection.url");
		String whDriverName = props.getProperty("local.wh.connection.driver_class");
		String whDbName = props.getProperty("local.wh.connection.database");
		String whUserName = props.getProperty("local.wh.connection.username");
		String whPassword = props.getProperty("local.wh.connection.password");

		whLocalDb = new DatabaseUtil(whUrl, whDbName, whDriverName, whUserName, whPassword);
		if (!whLocalDb.tryConnection()) {
			System.out.println("*** Warehouse database is not connected ***");
			return false;
		}
		UtilityCollection.getInstance().setWarehouseDb(whLocalDb);
		// System.out.println("*** Starting Email Engine ***");
		return startEmailEngine();

	}

	/**
	 * Read properties from properties file
	 */
	public void readProperties() {
		InputStream propFile;
		try {
			propFile = ClassLoaderUtil.getResourceAsStream(Constants.PROP_FILE_NAME, DatabaseConnection.class);
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

		guestUsername = props.getProperty("mail.user.username");
		guestPassword = props.getProperty("mail.user.password");
		UtilityCollection.minutes = props.getProperty("mail.notifiation.minute");
		UtilityCollection.seconds = props.getProperty("mail.notification.second");
		UtilityCollection.hours = props.getProperty("mail.notification.hour");
		UtilityCollection.getInstance().setProps(props);

		try {
			System.out.println("*** Values **" + props.isEmpty());
			System.out.println("*** Starting Email Engine ***");
			EmailEngine.instantiateEmailEngine(props);
			isEnginStart = true;

		} catch (EmailException e) {
			e.printStackTrace();
			log.warning("Email Engine Exception : " + e.getMessage());
			isEnginStart = false;
		}

		return isEnginStart;

	}

}
