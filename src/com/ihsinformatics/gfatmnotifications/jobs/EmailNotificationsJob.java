/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.table.TableStringConverter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.controllers.EmailController;
import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.model.ChilhoodFact;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.model.FastFact;
import com.ihsinformatics.gfatmnotifications.model.UtilityCollection;
import com.ihsinformatics.gfatmnotifications.ui.SwingControl;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.mysql.jdbc.Field;

import dnl.utils.text.table.TextTable;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class EmailNotificationsJob implements Job {
	private static final Logger log = Logger.getLogger(Class.class.getName());
	private DatabaseUtil localDb;
	private DatabaseUtil warehouseDb;
	private boolean filterDate = true;
	private DateTime dateFrom;
	private DateTime dateTo;
	private OpenMrsUtil warehouseOpenmrsInstance;
	private EmailController emailController;
	private Properties props;

	public EmailNotificationsJob() {
		warehouseDb = UtilityCollection.getWarehouseDb();
	}

	private void initialize(EmailNotificationsJob emailJob) {
		/*
		 * This code will be refactor.. props = UtilityCollection.getProps();
		 * warehouseDb = UtilityCollection.getWarehouseDb(); localDb
		 * =UtilityCollection.getLocalDb();
		 */
		warehouseDb = UtilityCollection.getWarehouseDb();
		setLocalDb(emailJob.getLocalDb());
		setOpenmrsWarehouse(emailJob.getOpenmrsWarehouse());
		setProps(emailJob.getProps());
		setEmailController(emailJob.getEmailController());
	}

	/*
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		 JobDataMap dataMap = context.getMergedJobDataMap();
		 EmailNotificationsJob emailJob = (EmailNotificationsJob) dataMap
				.get("emailJob");
		 initialize(emailJob);
		 // load all the site supervisor email from ware database.
		 warehouseOpenmrsInstance.LoadAllUsersEmail();
		 //remove when it move to production .
		 SwingControl swingControlDemo = new SwingControl();
		 swingControlDemo.showLabelDemoAfter();
		 //when we move to production level then we should take the datetime with .minusDays(1);
		 dateFrom = new DateTime().minusDays(1);
		 fastdailyEmailReportExecution(dateFrom);

	}

	public DatabaseUtil getLocalDb() {
		return localDb;
	}

	public void setLocalDb(DatabaseUtil localDb) {
		this.localDb = localDb;
	}

	public boolean isFilterDate() {
		return filterDate;
	}

	public void setFilterDate(boolean filterDate) {
		this.filterDate = filterDate;
	}

	public OpenMrsUtil getOpenmrsWarehouse() {
		return warehouseOpenmrsInstance;
	}

	public void setOpenmrsWarehouse(OpenMrsUtil openmrsWarehouse) {
		this.warehouseOpenmrsInstance = openmrsWarehouse;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public EmailController getEmailController() {
		return emailController;
	}

	public void setEmailController(EmailController emailController) {
		this.emailController = emailController;
	}

	/************************* Fast Email Execution *******************/
     
	public void fastdailyEmailReportExecution(DateTime dateFrom) {

		String todayDate = Constants.DATE_FORMATWH.format(dateFrom.toDate());
		// First we need to get All the Fast Fact-Table
		ArrayList<FastFact> factFast = warehouseOpenmrsInstance.getFactFast(todayDate);
		if (!factFast.isEmpty()) {
			for (FastFact factTable : factFast) {
				Email emailVal = warehouseOpenmrsInstance.getEmailByLocationId(factTable.getLocationId());
				if (emailVal == null) {
					log.warning("This Location:"+ factTable.getLocationDescription()+ " have not linked with any site supervisor email ");
				} else {
					factTable.setEmailAddress(emailVal.getEmailAdress());
					fastSiteSpecificScreenEmail(factTable);
				}
			 }
		} else {
			log.warning("No updates are avaiable...");
		}
	}
	
	/**
	 *  Responsibility is to check the conditions and build the content for emails to every receipeints
	 * @param factTable
	 * @return
	 */
	public boolean fastSiteSpecificScreenEmail(FastFact factTable) {
     
		String facilityName =factTable.getLocationDescription()+" ( "+factTable.getLocationName()+" )";
       
		Map<String,Integer> mapping = new HashMap<String,Integer>();
		    mapping.put("Verbal Screened",factTable.getTotalScreeingForm());  
	        mapping.put("Chest X-Rays",factTable.getChestXrays()); 
	        mapping.put("Verbal Screen Presumptives",factTable.getVerbalScreenPresumptives());  
	        mapping.put("Chest X-Ray Presumptives",factTable.getChestXrayPresumptives()); 
	        mapping.put("Samples Collected from Verbal Screen Presumptives",factTable.getSamplesCollectedVerbalScreenPresumptives());  
	        mapping.put("Samples Collected from CXR Presumptives",factTable.getSamplesCollectedCXRPresumptives()); 
	        mapping.put("GXP Tests Done",factTable.getGxpTestsDone());  
	        mapping.put("Internal Tests",factTable.getInternalTests()); 
	        mapping.put("External Tests",factTable.getExternalTests()); 
	        mapping.put("MTB+ [Internal]",factTable.getmTBpveInternal());
	        mapping.put("MTB+ [External]", factTable.getmTBpveExternal());
	        mapping.put("MTB+/RR+ [Internal]", factTable.getmTBpveRRpveInternal());
	        mapping.put("MTB+/RR+ [External]",factTable.getmTBpveRRpveExternal());
	        mapping.put("Error [all tests]", factTable.getAllError());
	        mapping.put("No result [all tests]",factTable.getNoResult());
	        mapping.put("Invalid [all tests]", factTable.getInvalidAllTest());
	        mapping.put("Pending Samples", factTable.getPendingSamples());
	        mapping.put("Clinically Diagnosed", factTable.getClinicallyDiagnosed());
	        mapping.put("Initiated on Antibiotic",factTable.getInitiatedOnAntibiotic());
	        mapping.put("Initiated on TB Tx", factTable.getInitiatedOnTBTx());
         
		String message =  tableFormate(mapping,facilityName);
		return sendEmail(factTable.getEmailAddress(), message, props.getProperty("mail.subject.title"));
	}

	/************************* Childhood Email Execution *******************/

	public void childhoodDailyReport(){
      
		ArrayList<ChilhoodFact> ctbFact= new ArrayList<ChilhoodFact>();
		
		
	}
	
	public boolean childhoodSiteSpecificScreenEmail(ChilhoodFact chilhoodFact){
		String facilityName =chilhoodFact.getLocationDescription()+" ( "+chilhoodFact.getLocationName()+" )";
		
		Map<String,Integer> mapping = new HashMap<String,Integer>();
			mapping.put("Screened by nurse",chilhoodFact.getScreenedByNurse());
			mapping.put("Presumptive by nurse",chilhoodFact.getPresumptiveByNurse());
			mapping.put("Screening Location Forms",chilhoodFact.getScreeningLocationForms());
			mapping.put("Registration forms ",chilhoodFact.getRegistrationForms());
			mapping.put("Presumptive Case Confirmed forms",chilhoodFact.getPresumptiveCaseConfirmedForms());
			mapping.put("TB Presumptive Confirmed",chilhoodFact.getTbPresumptiveConfirmed());
			mapping.put("Test indication ",chilhoodFact.getTestIndication());
			mapping.put("CBC Indicated",chilhoodFact.getCbcIndicated());
			mapping.put("ESR Indicated",chilhoodFact.getEsrIndicated());
			mapping.put("CXR Indicated",chilhoodFact.getCxrIndicated());
			mapping.put("MT Indicated",chilhoodFact.getMtIndicated());
			mapping.put("Ultrasound Indicated",chilhoodFact.getUltrasoundIndicated());
			mapping.put("Histopathology/FNAC Indicated",chilhoodFact.getHistopathologyFNACIndicated());
			mapping.put("CT scan Indicated",chilhoodFact.getCtScanIndicated());
			mapping.put("GXP Indicated",chilhoodFact.getGxpIndicated());
			mapping.put("TB Treatment intiated",chilhoodFact.getTbTreatmentIntiated());
			mapping.put("Antibiotic trial initiated",chilhoodFact.getAntibioticTrialInitiated());
			mapping.put("IPT treatment initiated",chilhoodFact.getIptTreatmentInitiated());
			mapping.put("TB Treatment Follow up forms",chilhoodFact.getTbTreatmentFUP());
			mapping.put("Antibiotic trial Follow up forms",chilhoodFact.getAntibioticTrialFUP());
			mapping.put("IPT follow up forms",chilhoodFact.getIptFUP());
			mapping.put("End of follow up forms",chilhoodFact.getEndOfFUP());
		
		String message =  tableFormate(mapping,facilityName);
		return sendEmail(chilhoodFact.getEmailAddress(), message,props.getProperty("mail.childhood.subject.title"));
	}
	
	
	/******************* Common Methods ************************/
	
    public boolean sendEmail(String email ,String message,String subject){
			
			boolean isSent = emailController.sendEmailWithHtml(email,
					subject, message,
					props.getProperty("mail.user.username"));
	
			return isSent;	
		}
	
    public String  tableFormate(Map<String, Integer>mapping ,String locationName){
    	
    	StringBuilder buf = new StringBuilder();
		buf.append("<html>" +
		           "<body>" +
		           "<table style='font-family: 'Trebuchet MS', Arial, Helvetica, sans-serif; border-spacing: 0px;  border-style:none; width: 100%;'>" +
		           "<caption style='padding: 8px; padding-top: 12px; padding-bottom: 12px;font-size: 150%;'><code>");
		 buf.append(locationName.toUpperCase());
		 buf.append("</code></caption>"+
		           "<tr>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Forms</th>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Count</th>" +
		           "</tr>");
		 for(Map.Entry m:mapping.entrySet()){  
			 buf.append("<tr><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		       .append(m.getKey())
		       .append("</td><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		       .append(m.getValue())
		       .append("</td></tr>");
		}
		buf.append("</table>" +
		           "</body>" +
		           "</html>");
		String html = buf.toString();
		
		return html;
    }
}
