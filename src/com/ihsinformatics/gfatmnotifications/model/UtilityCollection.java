package com.ihsinformatics.gfatmnotifications.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ihsinformatics.gfatmnotifications.EmailController;
import com.ihsinformatics.gfatmnotifications.OpenMrsUtil;

public class UtilityCollection {
	
	// static variable single_instance of type Singleton
    private static UtilityCollection instance = null;
	private static List<User> users;
	private static List<Location> locations;
	private static List<Patient> patients;
	private static List<String> userRoles;
	private static ArrayList<FactTable> factFast;
	/* private static List<Person> */
	private static Map<Integer, String> encounterTypes;
	private static List<Email> emailList;
	
	//Email Jobs  Instance 
	private OpenMrsUtil warehouseInstance;
	private EmailController emailController;
	private Properties props;
	
	
	
	private UtilityCollection(){
		
		/*users = new ArrayList<User>();
		locations = new ArrayList<Location>();
		patients = new ArrayList<Patient>();
		userRoles = new ArrayList<String>();
		emailList = new ArrayList<Email>();
		*/
	}
	
	
	 // static method to create instance of Singleton class
    public static UtilityCollection getInstance()
    {
        if (instance == null)
        	instance = new UtilityCollection();
 
        return instance;
    }
	
	
	
	/**
	 * @return the users
	 */
	public static List<User> getUsers() {
		return users;
	}
	/**
	 * @param users the users to set
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
	 * @param locations the locations to set
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
	 * @param patients the patients to set
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
	 * @param userRoles the userRoles to set
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
	 * @param encounterTypes the encounterTypes to set
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
	 * @param emailList the emailList to set
	 */
	public static void setEmailList(List<Email> emailList) {
		UtilityCollection.emailList = emailList;
	}

	/**
	 * @return the factFast
	 */
	public static ArrayList<FactTable> getFactFast() {
		return factFast;
	}

	/**
	 * @param factFast the factFast to set
	 */
	public static void setFactFast(ArrayList<FactTable> factFast) {
		UtilityCollection.factFast = factFast;
	}


	
	//Email job Intances 
	
	/**
	 * @return the warehouseInstance
	 */
	public OpenMrsUtil getWarehouseInstance() {
		return warehouseInstance;
	}


	/**
	 * @param warehouseInstance the warehouseInstance to set
	 */
	public void setWarehouseInstance(OpenMrsUtil warehouseInstance) {
		this.warehouseInstance = warehouseInstance;
	}


	/**
	 * @return the emailController
	 */
	public EmailController getEmailController() {
		return emailController;
	}


	/**
	 * @param emailController the emailController to set
	 */
	public void setEmailController(EmailController emailController) {
		this.emailController = emailController;
	}


	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}


	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}


	

}
