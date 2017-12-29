package com.ihsinformatics.gfatmnotifications.controllers;

import com.ihsinformatics.gfatmnotifications.model.FastFact;


public class DailyEmailNotification {

/**
 * This Class is under constructor -> Designer is working on this class 
 * 	
 */
	public DailyEmailNotification() {

	}
	
  public void programSelector(String programName,FastFact factTable){
	  
	  switch (programName) {
	case "FAST":
		fastDailyReport(factTable);
		break;
	case "Childhood":
		childhoodDailyReport();
		break;

	default:
		break;
	}
	  
  }

private void childhoodDailyReport() {
	
	
	
	
}

private void fastDailyReport(FastFact factTable) {
	
	//First we need to work
	
}	
	
	
}
