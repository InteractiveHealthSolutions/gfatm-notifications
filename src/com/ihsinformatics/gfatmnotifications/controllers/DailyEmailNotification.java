package com.ihsinformatics.gfatmnotifications.controllers;

import com.ihsinformatics.gfatmnotifications.model.FactTable;


public class DailyEmailNotification {

/**
 * This Class is under constructor -> Designer is working on this class 
 * 	
 */
	public DailyEmailNotification() {

	}
	
  public void programSelector(String programName,FactTable factTable){
	  
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

private void fastDailyReport(FactTable factTable) {
	
	//First we need to work
	
}	
	
	
}
