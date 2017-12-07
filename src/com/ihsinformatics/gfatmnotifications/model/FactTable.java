package com.ihsinformatics.gfatmnotifications.model;

public class FactTable {

	private  Integer locationId;
	private  String locationName;
	private  String locationDescription;
	private  String dateTime;
	private  Integer totalScreeingForm;
	private  Integer totalChestXrays;
	private  Integer totalVerbalScreenPresumptives;
	private  Integer totalChestXrayPresumptives;
	private  Integer totalVerbalScreenAndChestXrayPresumptives;
	private  Integer  SamplesCollectedVerbalScreenPresumptives;
	private  Integer  SamplesCollectedCXRPresumptives;
	private  Integer  AcceptedSamples;
	private  Integer  InternalTests;
	private  Integer  ExternalTests;
	private  Integer  GXPTestsDone;
	private  Integer  MTBpveInternal;
	private  Integer  MTBpveRRpveInternal;
	private  Integer  ErrorInternal;
	private  Integer  NoresultInternal;
	private  Integer  InvalidInternal;
	private  Integer  PendingSamples;
	private  Integer  ClinicallyDiagnosed;
	private  Integer  InitiatedOnAntibiotic;
	private  Integer  InitiatedOnTBTx;
	private  String   emailAddress;
	
	
	
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getLocationDescription() {
		return locationDescription;
	}
	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public Integer getTotalScreenForm() {
		return totalScreeingForm;
	}
	public void setTotalScreenForm(Integer totalScreenForm) {
		this.totalScreeingForm = totalScreenForm;
	}
	public Integer getTotalChestXrays() {
		return totalChestXrays;
	}
	public void setTotalChestXrays(Integer totalChestXrays) {
		this.totalChestXrays = totalChestXrays;
	}
	public Integer getTotalVerbalScreenPresumptives() {
		return totalVerbalScreenPresumptives;
	}
	public void setTotalVerbalScreenPresumptives(
			Integer totalVerbalScreenPresumptives) {
		this.totalVerbalScreenPresumptives = totalVerbalScreenPresumptives;
	}
	public Integer getTotalChestXrayPresumptives() {
		return totalChestXrayPresumptives;
	}
	public void setTotalChestXrayPresumptives(Integer totalChestXrayPresumptives) {
		this.totalChestXrayPresumptives = totalChestXrayPresumptives;
	}
	public Integer getTotalVerbalScreen() {
		return totalVerbalScreenAndChestXrayPresumptives;
	}
	public void setTotalVerbalScreen(Integer totalVerbalScreen) {
		this.totalVerbalScreenAndChestXrayPresumptives = totalVerbalScreen;
	}
	public Integer getTotalVerbalScreenAndChestXrayPresumptives() {
		return totalVerbalScreenAndChestXrayPresumptives;
	}
	public void setTotalVerbalScreenAndChestXrayPresumptives(
			Integer totalVerbalScreenAndChestXrayPresumptives) {
		this.totalVerbalScreenAndChestXrayPresumptives = totalVerbalScreenAndChestXrayPresumptives;
	}
	public Integer getSamplesCollectedVerbalScreenPresumptives() {
		return SamplesCollectedVerbalScreenPresumptives;
	}
	public void setSamplesCollectedVerbalScreenPresumptives(
			Integer samplesCollectedVerbalScreenPresumptives) {
		SamplesCollectedVerbalScreenPresumptives = samplesCollectedVerbalScreenPresumptives;
	}
	public Integer getSamplesCollectedCXRPresumptives() {
		return SamplesCollectedCXRPresumptives;
	}
	public void setSamplesCollectedCXRPresumptives(
			Integer samplesCollectedCXRPresumptives) {
		SamplesCollectedCXRPresumptives = samplesCollectedCXRPresumptives;
	}
	public Integer getAcceptedSamples() {
		return AcceptedSamples;
	}
	public void setAcceptedSamples(Integer acceptedSamples) {
		AcceptedSamples = acceptedSamples;
	}
	public Integer getInternalTests() {
		return InternalTests;
	}
	public void setInternalTests(Integer internalTests) {
		InternalTests = internalTests;
	}
	public Integer getExternalTests() {
		return ExternalTests;
	}
	public void setExternalTests(Integer externalTests) {
		ExternalTests = externalTests;
	}
	public Integer getGXPTestsDone() {
		return GXPTestsDone;
	}
	public void setGXPTestsDone(Integer gXPTestsDone) {
		GXPTestsDone = gXPTestsDone;
	}
	public Integer getMTBpveInternal() {
		return MTBpveInternal;
	}
	public void setMTBpveInternal(Integer mTBpveInternal) {
		MTBpveInternal = mTBpveInternal;
	}
	public Integer getMTBpveRRpveInternal() {
		return MTBpveRRpveInternal;
	}
	public void setMTBpveRRpveInternal(Integer mTBpveRRpveInternal) {
		MTBpveRRpveInternal = mTBpveRRpveInternal;
	}
	public Integer getErrorInternal() {
		return ErrorInternal;
	}
	public void setErrorInternal(Integer errorInternal) {
		ErrorInternal = errorInternal;
	}
	public Integer getNoresultInternal() {
		return NoresultInternal;
	}
	public void setNoresultInternal(Integer noresultInternal) {
		NoresultInternal = noresultInternal;
	}
	public Integer getInvalidInternal() {
		return InvalidInternal;
	}
	public void setInvalidInternal(Integer invalidInternal) {
		InvalidInternal = invalidInternal;
	}
	public Integer getPendingSamples() {
		return PendingSamples;
	}
	public void setPendingSamples(Integer pendingSamples) {
		PendingSamples = pendingSamples;
	}
	public Integer getClinicallyDiagnosed() {
		return ClinicallyDiagnosed;
	}
	public void setClinicallyDiagnosed(Integer clinicallyDiagnosed) {
		ClinicallyDiagnosed = clinicallyDiagnosed;
	}
	public Integer getInitiatedOnAntibiotic() {
		return InitiatedOnAntibiotic;
	}
	public void setInitiatedOnAntibiotic(Integer initiatedOnAntibiotic) {
		InitiatedOnAntibiotic = initiatedOnAntibiotic;
	}
	public Integer getInitiatedOnTBTx() {
		return InitiatedOnTBTx;
	}
	public void setInitiatedOnTBTx(Integer initiatedOnTBTx) {
		InitiatedOnTBTx = initiatedOnTBTx;
	}

	
}
