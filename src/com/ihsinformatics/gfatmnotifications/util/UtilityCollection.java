package com.ihsinformatics.gfatmnotifications.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ihsinformatics.gfatmnotifications.controllers.EmailController;
import com.ihsinformatics.gfatmnotifications.model.ChilhoodFact;
import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.model.FastFact;
import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.model.PatientScheduled;
import com.ihsinformatics.gfatmnotifications.model.PetFact;
import com.ihsinformatics.gfatmnotifications.model.User;
import com.ihsinformatics.util.DatabaseUtil;

public class UtilityCollection {

	private static UtilityCollection		instance	= null;
	// static variable should be remove...
	private static List<User>				users;
	private static List<Location>			locations;
	private static List<Patient>			patients;
	private static List<String>				userRoles;
	private static ArrayList<FastFact>		factFast;
	private static ArrayList<ChilhoodFact>	factChildhood;
	private static ArrayList<PetFact>		factPet;
	private ArrayList<PatientScheduled>		patientScheduledsList;

	/* private static List<Person> */
	private static Map<Integer, String>		encounterTypes;
	private static List<Email>				emailList;

	// Email Jobs Instance
	private static OpenMrsUtil				warehouseInstance;
	private static EmailController			emailController;
	private static Properties				props;
	private static DatabaseUtil				warehouseDb;
	private static DatabaseUtil				localDb;

	public static String					minutes;
	public static String					seconds;
	public static String					hours;

	private UtilityCollection() {

		/*
		 * users = new ArrayList<User>(); locations = new ArrayList<Location>();
		 * patients = new ArrayList<Patient>(); userRoles = new
		 * ArrayList<String>(); emailList = new ArrayList<Email>();
		 */
	}

	// static method to create instance of Singleton class
	public static UtilityCollection getInstance() {
		if (instance == null) {
			instance = new UtilityCollection();
		}

		return instance;
	}

	/**
	 * @return the users
	 */
	public static List<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public static void setUsers(List<User> users) {
		UtilityCollection.users = users;
	}

	/**
	 * @return the locations
	 */
	public static List<Location> getLocations() {
		return locations;
	}

	/**
	 * @param locations
	 *            the locations to set
	 */
	public static void setLocations(List<Location> locations) {
		UtilityCollection.locations = locations;
	}

	/**
	 * @return the patients
	 */
	public static List<Patient> getPatients() {
		return patients;
	}

	/**
	 * @param patients
	 *            the patients to set
	 */
	public static void setPatients(List<Patient> patients) {
		UtilityCollection.patients = patients;
	}

	/**
	 * @return the userRoles
	 */
	public static List<String> getUserRoles() {
		return userRoles;
	}

	/**
	 * @param userRoles
	 *            the userRoles to set
	 */
	public static void setUserRoles(List<String> userRoles) {
		UtilityCollection.userRoles = userRoles;
	}

	/**
	 * @return the encounterTypes
	 */
	public static Map<Integer, String> getEncounterTypes() {
		return encounterTypes;
	}

	/**
	 * @param encounterTypes
	 *            the encounterTypes to set
	 */
	public static void setEncounterTypes(Map<Integer, String> encounterTypes) {
		UtilityCollection.encounterTypes = encounterTypes;
	}

	/**
	 * @return the emailList
	 */
	public static List<Email> getEmailList() {
		return emailList;
	}

	/**
	 * @param emailList
	 *            the emailList to set
	 */
	public static void setEmailList(List<Email> emailList) {
		UtilityCollection.emailList = emailList;
	}

	/**
	 * @return the factFast
	 */
	public static ArrayList<FastFact> getFactFast() {
		return factFast;
	}

	/**
	 * @param factFast
	 *            the factFast to set
	 */
	public static void setFactFast(ArrayList<FastFact> factFast) {
		UtilityCollection.factFast = factFast;
	}

	/**
	 * @return the warehouseInstance
	 */
	public static OpenMrsUtil getWarehouseInstance() {
		return warehouseInstance;
	}

	/**
	 * @param warehouseInstance
	 *            the warehouseInstance to set
	 */
	public static void setWarehouseInstance(OpenMrsUtil warehouseInstance) {
		UtilityCollection.warehouseInstance = warehouseInstance;
	}

	/**
	 * @return the emailController
	 */
	public static EmailController getEmailController() {
		return emailController;
	}

	/**
	 * @param emailController
	 *            the emailController to set
	 */
	public static void setEmailController(EmailController emailController) {
		UtilityCollection.emailController = emailController;
	}

	/**
	 * @return the props
	 */
	public static Properties getProps() {
		return props;
	}

	/**
	 * @param props
	 *            the props to set
	 */
	public static void setProps(Properties props) {
		UtilityCollection.props = props;
	}

	/**
	 * @return the warehouseDb
	 */
	public static DatabaseUtil getWarehouseDb() {
		return warehouseDb;
	}

	/**
	 * @param warehouseDb
	 *            the warehouseDb to set
	 */
	public static void setWarehouseDb(DatabaseUtil warehouseDb) {
		UtilityCollection.warehouseDb = warehouseDb;
	}

	/**
	 * @return the localDb
	 */
	public static DatabaseUtil getLocalDb() {
		return localDb;
	}

	/**
	 * @param localDb
	 *            the localDb to set
	 */
	public static void setLocalDb(DatabaseUtil localDb) {
		UtilityCollection.localDb = localDb;
	}

	/**
	 * @return the factPet
	 */
	public static ArrayList<PetFact> getFactPet() {
		return factPet;
	}

	/**
	 * @param factPet
	 *            the factPet to set
	 */
	public static void setFactPet(ArrayList<PetFact> factPet) {
		UtilityCollection.factPet = factPet;
	}

	/**
	 * @return the factChildhood
	 */
	public static ArrayList<ChilhoodFact> getFactChildhood() {
		return factChildhood;
	}

	/**
	 * @param factChildhood
	 *            the factChildhood to set
	 */
	public static void setFactChildhood(ArrayList<ChilhoodFact> factChildhood) {
		UtilityCollection.factChildhood = factChildhood;
	}

	/**
	 * @return the patientScheduledsList
	 */
	public ArrayList<PatientScheduled> getPatientScheduledsList() {
		return patientScheduledsList;
	}

	/**
	 * @param patientScheduledsList
	 *            the patientScheduledsList to set
	 */
	public void setPatientScheduledsList(
			ArrayList<PatientScheduled> patientScheduledsList) {
		this.patientScheduledsList = patientScheduledsList;
	}

}
