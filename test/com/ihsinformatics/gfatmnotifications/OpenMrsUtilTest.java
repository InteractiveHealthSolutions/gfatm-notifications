package com.ihsinformatics.gfatmnotifications;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.User;
import com.ihsinformatics.gfatmnotifications.model.UtilityCollection;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.util.DatabaseUtil;

public class OpenMrsUtilTest {

	private static final String userHome = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "gfatm";
	private static String propFilePath = userHome
			+ System.getProperty("file.separator")
			+ "gfatm-notifications.properties";
	private static Properties props;
	private static DatabaseUtil localDb;
	private static OpenMrsUtil util;

	@BeforeClass
	public static void setUp() throws IOException {
		System.out.println("*** Starting up ***");
		System.out.println("Reading properties...");
		System.out.println(propFilePath);
		InputStream propFile = new FileInputStream(propFilePath);
		if (propFile != null) {
			props = new Properties();
			props.load(propFile);
		}
		String url = props.getProperty("hibernate.connection.url");
		String driverName = props
				.getProperty("hibernate.connection.driver_class");
		String dbName = props.getProperty("hibernate.connection.database");
		String userName = props.getProperty("hibernate.connection.username");
		String password = props.getProperty("hibernate.connection.password");
		System.out.println(url + " " + dbName + " " + driverName + " "
				+ userName + " " + password);
		localDb = new DatabaseUtil(url, dbName, driverName, userName, password);
		if (!localDb.tryConnection()) {
			System.out
					.println("Failed to connect with local database. Exiting");
			System.exit(-1);
		}
		util = new OpenMrsUtil(localDb);
	}

	@Test
	public final void testLoadEncounterTypes() {
		OpenMrsUtil.setEncounterTypes(null);
		util.loadEncounterTypes();
		Map<Integer, String> encounterTypes = OpenMrsUtil.getEncounterTypes();
		Assert.assertTrue("Should load encounter types",
				encounterTypes.size() > 0);
	}

	@Test
	public final void testLoadLocations() {
		UtilityCollection.setLocations(null);
		util.loadLocations();
		List<Location> locations = UtilityCollection.getLocations();
		Assert.assertTrue("Should load locations", locations.size() > 0);
	}

	@Test
	public final void testLoadUsers() {
		UtilityCollection.setUsers(null);
		util.loadUsers();
		List<User> users = UtilityCollection.getUsers();
		Assert.assertTrue("Should load users", users.size() > 0);
	}

	@Test
	public final void testGetEncounter() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetEncounters() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetEncounterObservations() {
		fail("Not yet implemented"); // TODO
	}

}
