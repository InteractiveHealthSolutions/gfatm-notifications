package com.ihsinformatics.gfatmnotifications.model;
/**
 * @author Shujaat
 * fup->Follow up
 * ra -> result aviable 
 */

public class PatientScheduled {
  
	private Integer patientId; 
	private String  patientIdentifier; 
	private String  reasonForCall; 
	private String  fupFacilityScheduled; 
	private String  fupFacilityVisitDate;
	private String  testType;
	private String  raFacilityScheduled;
	private String  raFacilityVisitDate;
	
	
	/**
	 * @return the patientId
	 */
	public Integer getPatientId() {
		return patientId;
	}
	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	/**
	 * @return the patientIdentifier
	 */
	public String getPatientIdentifier() {
		return patientIdentifier;
	}
	/**
	 * @param patientIdentifier the patientIdentifier to set
	 */
	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}
	/**
	 * @return the reasonForCall
	 */
	public String getReasonForCall() {
		return reasonForCall;
	}
	/**
	 * @param reasonForCall the reasonForCall to set
	 */
	public void setReasonForCall(String reasonForCall) {
		this.reasonForCall = reasonForCall;
	}
	/**
	 * @return the fupFacilityScheduled
	 */
	public String getFupFacilityScheduled() {
		return fupFacilityScheduled;
	}
	/**
	 * @param fupFacilityScheduled the fupFacilityScheduled to set
	 */
	public void setFupFacilityScheduled(String fupFacilityScheduled) {
		this.fupFacilityScheduled = fupFacilityScheduled;
	}
	/**
	 * @return the fupFacilityVisitDate
	 */
	public String getFupFacilityVisitDate() {
		return fupFacilityVisitDate;
	}
	/**
	 * @param fupFacilityVisitDate the fupFacilityVisitDate to set
	 */
	public void setFupFacilityVisitDate(String fupFacilityVisitDate) {
		this.fupFacilityVisitDate = fupFacilityVisitDate;
	}
	/**
	 * @return the testType
	 */
	public String getTestType() {
		return testType;
	}
	/**
	 * @param testType the testType to set
	 */
	public void setTestType(String testType) {
		this.testType = testType;
	}
	/**
	 * @return the raFacilityScheduled
	 */
	public String getRaFacilityScheduled() {
		return raFacilityScheduled;
	}
	/**
	 * @param raFacilityScheduled the raFacilityScheduled to set
	 */
	public void setRaFacilityScheduled(String raFacilityScheduled) {
		this.raFacilityScheduled = raFacilityScheduled;
	}
	/**
	 * @return the raFacilityVisitDate
	 */
	public String getRaFacilityVisitDate() {
		return raFacilityVisitDate;
	}
	/**
	 * @param raFacilityVisitDate the raFacilityVisitDate to set
	 */
	public void setRaFacilityVisitDate(String raFacilityVisitDate) {
		this.raFacilityVisitDate = raFacilityVisitDate;
	}

	
	
	
}
