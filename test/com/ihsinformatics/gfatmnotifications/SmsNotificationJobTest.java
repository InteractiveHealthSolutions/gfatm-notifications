package com.ihsinformatics.gfatmnotifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.jobs.SmsNotificationsJob;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

public class SmsNotificationJobTest {

	private DateTime			dateFrom;
	@SuppressWarnings("unused")
	private DateTime			dateTo;
	SmsNotificationsJob			sms;
	OpenMrsUtil					openMrs;
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
	private static DatabaseUtil	localDb;

	@Before
	public void setUp() throws Exception {
		sms = new SmsNotificationsJob();
		System.out.println("*** Starting up " + title + " ***");
		System.out.println("Reading properties...");
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
		dateFrom = new DateTime();
		dateFrom.minusHours(Constants.SMS_SCHEDULE_INTERVAL_IN_HOURS);
		dateTo = new DateTime();

		openMrs = new OpenMrsUtil(localDb);
		openMrs.loadLocations();
		openMrs.loadUsers();
		openMrs.loadPatients();
		openMrs.loadEncounterTypes();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * I have get one exception while passning august encouters parameters. I
	 * think this hasn't included the tb_patient variable if we get this type of
	 * encounter which doesn't have tb_patient than it fail our assertion
	 * testing. tb_patient is must according to codebook. Some cases depends
	 * upon the execution time.
	 *
	 */

	public void sendTreatmentInitiationSmsTest() {

		try {

			Encounter enc = openMrs.getEncounter(7253, 29);
			Map<String, Object> observations = openMrs
					.getEncounterObservations(enc);
			enc.setObservations(observations);
			// System.out.println(enc.getEncounterType());
			SmsController smsController = new SmsController(
					Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
					Constants.SMS_USE_SSL);
			boolean response = sendTreatmentInitiationTestSms(enc,
					smsController, openMrs);
			System.out.println(response);
			Assert.assertTrue(response);

		} catch (Exception e) {

			Assert.fail("Exception: " + e.getMessage());
		}

	}

	/**
	 * Refferal notification is for Field Supervisor In this cases we test the
	 * successful test senior, while pass expected encounter and encoutertype.
	 * The result should be true.
	 */

	public void sendReferreLSmsTest() {

		try {

			Encounter encounter = openMrs.getEncounter(7262, 28);
			Map<String, Object> observation = openMrs
					.getEncounterObservations(encounter);
			encounter.setObservations(observation);
			SmsController smsController = new SmsController(
					Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
					Constants.SMS_USE_SSL);
			boolean response = sendReferralFormSms(encounter, smsController);
			Assert.assertFalse(response);

		} catch (Exception e) {

			Assert.fail("Exception: " + e.getMessage());
		}

	}

	/**
	 * In this cases we test our method with unexpect parameters.
	 */

	public void shouldNotsendReferrelSmsTest() {
		try {
			Encounter encounter = openMrs.getEncounter(7262, 28);
			Map<String, Object> observation = openMrs
					.getEncounterObservations(encounter);
			encounter.setObservations(observation);
			SmsController smsController = new SmsController(
					Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
					Constants.SMS_USE_SSL);
			boolean response = sendReferralFormSms(encounter, smsController);
			Assert.assertTrue(response);

		} catch (Exception e) {

			Assert.fail("Exception: " + e.getMessage());
		}

	}

	@Test
	public void sendTreatmentFollowupSmsTest() {

		try {
			Encounter encounter = openMrs.getEncounter(7289, 30);
			Map<String, Object> observation = openMrs
					.getEncounterObservations(encounter);
			encounter.setObservations(observation);
			SmsController smsController = new SmsController(
					Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY,
					Constants.SMS_USE_SSL);
			boolean response = sendTreatmentFollowupSms(encounter,
					smsController);
			Assert.assertTrue(response);

		} catch (Exception e) {

			Assert.fail("Exception: " + e.getMessage());
		}

	}

	/**
	 * I copy this methods from the SmsNotificationJob for testing purpose.
	 *
	 * @param encounter
	 * @param smsController
	 * @param openmrsTestInstant
	 * @return
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	public boolean sendTreatmentInitiationTestSms(Encounter encounter,
			SmsController smsController, OpenMrsUtil openmrsTestInstant) {
		Date returnVisitDate = null;
		dateFrom = dateFrom.minusHours(24);
		Map<String, Object> observation = encounter.getObservations();
		System.out.print(observation.toString());
		String rtnVisitDate = observation.get("return_visit_date").toString()
				.toUpperCase();
		String isTbPatient = observation.get("tb_patient").toString()
				.toUpperCase();
		String antibiotic = "";
		Patient patient = openmrsTestInstant.getPatientByIdentifier(encounter
				.getIdentifier());

		/*
		 * Map<String, Object> EndOfFollowUpObservation = null; List<Encounter>
		 * encounterEndFup = openmrs.getEncounters(dateFrom, dateTo,
		 * Constants.END_FOLLOWUP_FORM_ENCOUNTER_TYPE); for (Encounter
		 * endFupEncounter : encounterEndFup) { if
		 * (endFupEncounter.getIdentifier().equals( encounter.getIdentifier()))
		 * {
		 *
		 * Map<String, Object> observations = getObservations(endFupEncounter);
		 * endFupEncounter.setObservations(observations);
		 * EndOfFollowUpObservation = endFupEncounter.getObservations(); } }
		 */
		// we need FAST end of follow up form data.
		Map<String, Object> EndOfFollowUpObservation = null;
		Encounter encounterEndFup = openmrsTestInstant
				.getEncounterByPatientIdentifier(encounter.getIdentifier(),
						Constants.END_FOLLOWUP_FORM_ENCOUNTER_TYPE);
		Map<String, Object> observations = getObservations(encounterEndFup);
		encounterEndFup.setObservations(observations);
		EndOfFollowUpObservation = encounterEndFup.getObservations();

		try {

			returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,
					DateTimeUtil.SQL_DATETIME);

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		/****************** CONDITIONS *************************************/
		// Check the return visit date is in past or not ...
		if (returnVisitDate.before(new Date())) {
			return false;
		} else if (isTbPatient.equals("INCONCLUSIVE")) {
			antibiotic = observation.get("antibiotic").toString().toUpperCase();
			if (antibiotic.equals("NO")) {
				return false;
			}
		} else if (isTbPatient.equals("YES")) {
			return false;
		}
		/*
		 * if (!EndOfFollowUpObservation.get("treatment_outcome").equals(
		 * "ANTIBIOTIC COMPLETE - NO TB")) { return false; }
		 */
		// we need to check the treatment_outcome from end of follow of form
		if (rtnVisitDate.equals(null) || patient.isDead()) {
			return false;
		}

		/************************ MAKEUP THE SMS ***************/
		try {
			String sendTo = encounter.getPatientContact();
			// System.out.println(sendTo);

			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			Location referralLocation = null;

			/**
			 * if we have referrel site then why we check the
			 */
			String id = openmrsTestInstant.checkReferelPresent(encounter);
			if (!id.equals("")) {

				Encounter ency = openmrsTestInstant.getEncounter(
						Integer.parseInt(id), 28);
				observation = openmrsTestInstant.getEncounterObservations(ency);
				ency.setObservations(observation);
				String referralSite = observation.get("referral_site")
						.toString();
				referralLocation = openmrsTestInstant
						.getLocationByShortCode(referralSite);
				encounter.setLocation(referralLocation.getName());

			}
			Constants.DATE_FORMAT.applyPattern("EEEE d MMM yyyy");
			/* String df=DateFormat.getDateInstance().format(dueDate.getTime()); */

			/********************* antibiotic = yes then message change ***************/
			StringBuilder message = new StringBuilder();
			if (isTbPatient.equals("INCONCLUSIVE")) {

				message.append("Janab " + encounter.getPatientName() + ", ");
				message.append("" + encounter.getLocation());
				message.append(" pe ap ko doctor ke paas "
						+ Constants.DATE_FORMAT.format(returnVisitDate)
						+ " ko moainey ke liyey tashreef lana hai. "
						+ "Agar is mutaliq ap kuch poochna chahain tou Aao TB Mitao "
						+ "helpline 021-111-111-982 pe rabta karain.");
			} else {

				message.append("Janab " + encounter.getPatientName() + ", ");
				message.append("" + encounter.getLocation());
				message.append(" pe ap ko doctor ke paas "
						+ Constants.DATE_FORMAT.format(returnVisitDate)
						+ "ko moainey aur adwiyaat hasil karne ke liyey "
						+ "tashreef lana hai. Agar is mutaliq ap kuch poochna chahain tou Aao TB Mitao "
						+ "helpline 021-111-111-982 pe rabta karain.");
			}

			// sendTo = "03222808980";
			System.out.println(dueDate.getTime());
			sendTo = sendTo.replace("-", "");
			String response = smsController
					.createSms(sendTo, message.toString(), dueDate.getTime(),
							"TEST", rtnVisitDate);
			System.out.println(response);
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendReferralFormSms(Encounter encounter,
			SmsController smsController) {

		Map<String, Object> observations = encounter.getObservations();
		DateTime dueDate = new DateTime(encounter.getEncounterDate());
		// dueDate = dueDate.minusDays(3);
		// date<=curren

		try {

			dueDate = dueDate.plusDays(1);
			Date parsDate = new SimpleDateFormat("dd-MMM-yyyy")
			.parse(Constants.DATE_FORMAT.format(dueDate.toDate()));
			Date currentDate = new SimpleDateFormat("dd-MMM-yyyy")
			.parse(Constants.DATE_FORMAT.format(new Date()));
			if (parsDate.before(currentDate)) {
				return false;
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// dueDate = DateTime.now();
		String sendTo;
		String siteSupervisorName;

		Object referredOrTransferred = observations.get("referral_transfer");
		if (referredOrTransferred.equals(Constants.PATIENT_REFERRED)
				|| referredOrTransferred.equals(Constants.PATIENT_TRANSFERRED)) {

			String referralSite = observations.get("referral_site").toString();
			Location referralLocation = openMrs
					.getLocationByShortCode(referralSite);

			if (referralLocation == null) {
				return false;
			}
			/**
			 * In case of primary contact is empty then we go with secondary
			 * contact number if we have both are missing then we return false.
			 */
			if (referralLocation.getPrimaryContact() != null) {

				sendTo = referralLocation.getPrimaryContact();
				siteSupervisorName = referralLocation.getPrimaryContactName();

			} else if (referralLocation.getSecondaryContact() != null) {

				sendTo = referralLocation.getPrimaryContact();
				siteSupervisorName = referralLocation.getSecondaryContactName();

			} else {
				return false;
			}
			// create message for site supervisor.
			StringBuilder message = new StringBuilder();
			message.append("Janab " + siteSupervisorName + ",");
			message.append("ap ke markaz " + referralLocation.getName()
					+ " pe aik mareez " + encounter.getIdentifier() + " ");
			message.append("ko muntaqil kiya ja raha hai. Is mareez say rabta karain.");
			try {
				sendTo = sendTo.replace("-", "");
				smsController.createSms(sendTo, message.toString(),
						dueDate.toDate(), "TEST", ""); // need to write FAST
				log.info(message.toString());
			} catch (Exception e) {
				log.warning(e.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean sendTreatmentFollowupSms(Encounter encounter,
			SmsController smsController) {
		Date returnVisitDate = null;
		Map<String, Object> observations = encounter.getObservations();
		String returnVisitStr = observations.get("return_visit_date")
				.toString().toUpperCase();
		if (returnVisitStr.equals(null)
				|| !openMrs.isTransferOrReferel(encounter)) {
			return false;
		}
		try {
			// Past date is not allowed so, we ignore or skip the past date
			// notifications
			returnVisitDate = DateTimeUtil.getDateFromString(returnVisitStr,
					DateTimeUtil.SQL_DATETIME);
			Date currentDate = new SimpleDateFormat("dd-MMM-yyyy")
			.parse(Constants.DATE_FORMAT.format(new Date()));

			if (returnVisitDate.before(currentDate)) {
				return false;
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String id = openMrs.checkReferelPresent(encounter);
		Location referralLocation = null;

		if (!id.equals("")) {

			Encounter ency = openMrs.getEncounter(Integer.parseInt(id), 28);
			observations = openMrs.getEncounterObservations(ency);
			ency.setObservations(observations);
			String referralSite = observations.get("referral_site").toString();
			referralLocation = openMrs.getLocationByShortCode(referralSite);
			encounter.setLocation(referralLocation.getName());

		}

		try {

			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(returnVisitDate);
			dueDate.set(Calendar.DATE, dueDate.get(Calendar.DATE) - 1);
			String sendTo = encounter.getPatientContact();
			sendTo = sendTo.replace("-", "");

			/**
			 * build Custom message..
			 */
			System.out.print("" + dueDate.getTime());
			StringBuilder message = new StringBuilder();
			message.append("Janab " + encounter.getPatientName() + ",");
			message.append("" + encounter.getLocation()
					+ " pe ap ko doctor ke paas ");
			Constants.DATE_FORMAT.applyPattern("EEEE d MMM yyyy");
			message.append(Constants.DATE_FORMAT.format(returnVisitDate) + " ");
			message.append("ko moainey aur adwiyaat hasil karne ke liyey tashreef lana hai. ");
			message.append("Agar is mutaliq ap kuch poochna chahain tou AaoTBMitao helpline ");
			message.append("021-111-111-982 pe rabta karain.");
			// send message to patient.
			smsController.createSms(sendTo, message.toString(),
					dueDate.getTime(), "Test", "");

			log.info(message.toString());
		} catch (Exception e) {
			log.warning(e.getMessage());
			return false;
		}
		return true;
	}

	public Map<String, Object> getObservations(Encounter encounter) {

		Map<String, Object> observations = openMrs
				.getEncounterObservations(encounter);
		return observations;
	}

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
