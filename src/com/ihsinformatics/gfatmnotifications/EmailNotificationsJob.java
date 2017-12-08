/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
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
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.model.FactTable;
import com.ihsinformatics.gfatmnotifications.model.UtilityCollection;
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
		 * This  code will be refactor..
	    props = UtilityCollection.getProps();
	    warehouseDb = UtilityCollection.getWarehouseDb();
	    localDb =UtilityCollection.getLocalDb();
	    */
		
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

		dateFrom = new DateTime()
				.minusHours(Constants.EMAIL_SCHEDULE_INTERVAL_IN_HOURS);
		dateTo = new DateTime();
		fastdailyEmailReportExecution(dateFrom, dateTo);

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

	public void fastdailyEmailReportExecution(DateTime dateFrom, DateTime dateTo) {

		String dFrom = Constants.DATE_FORMAT.format(dateFrom.toDate());
		String dTo = Constants.DATE_FORMAT.format(dateTo.toDate());
		// First we need to get All the Fast Fact-Table
		ArrayList<FactTable> factFast = warehouseOpenmrsInstance.getFactFast(dFrom,
				dTo);
		
		for (FactTable factTable : factFast) {
			Email emailVal = warehouseOpenmrsInstance.getEmailByLocationName(factTable
					.getLocationId());
			if (emailVal == null) {
				log.warning("This Location:"
						+ factTable.getLocationDescription()
						+ " have not linked with any site supervisor");
			} else {
				factTable.setEmailAddress(emailVal.getEmailAdress());
			 	//fastSiteSpecificScreenEmail(factTable);
			}
		}

	}

	// Responsibility is to check the conditions and build the content for
	// emails to every receipeints
	public boolean fastSiteSpecificScreenEmail(FactTable factTable) {

		StringBuilder message = getFormatedMessage(factTable);
		boolean isSent = emailController.sendEmailWithHtml(
				factTable.getEmailAddress(),
				props.getProperty("mail.subject.title"), message.toString(),
				props.getProperty("mail.user.username"));
		return isSent;
	}

	// utils.text.table.TextTable..To generating the text table/not supported the java 7
	public void generateSubtitleSelectionOutput(FactTable factTable) {

		String[] columnNames = { "FORMS", "Count" };
		Object[][] dataTable = new Object[10][columnNames.length];
		for (int i = 0; i < 10; i++) {
			dataTable[i] = new Object[] { factTable.getTotalVerbalScreen(),
					factTable.getLocationName() };
		}
		TextTable tt = new TextTable(columnNames, dataTable);
		tt.setAddRowNumbering(true);
		tt.printTable();
	}

	// Email format in table using html tag with stringBuilder
	public StringBuilder getFormatedMessage(FactTable factTable) {

		StringBuilder emailString = new StringBuilder();
		String nameString = factTable.getLocationDescription() + " ( "
				+ factTable.getLocationName() + ")";
		emailString
				.append("<html><body>"
						+ "<table style='border: 0px solid ; font-size: 11px;color: #333333;border-width: 1px;border-color: #3A3A3A;border-collapse: collapse;font-family: verdana, arial, sans-serif;'>");
		emailString.append("<caption>");
		emailString
				.append("<img src='http://chart.apis.google.com/chart?cht=p3&chs=500x250&chdl=first+legend%7Csecond+legend%7Cthird+legend%7Cfourth+legend&chl=first+label%7Csecond+label%7Cthird+label%7Cfourth+label&chco=FF0000|00FFFF|00FF00|CCC,6699CC|CC33FF|CCCC33&chtt=FAST+DAILY+REPORT&chts=000000,24&chd=t:5,10,50,10,30|25,35,45' />");
		emailString.append("</caption>");
		emailString
				.append("<caption style='background-color: #32CD32 ; border: 1px solid black;  padding:10px; color: white;font-family:Helvetica, sans-serif;'>");
		emailString.append(nameString.toUpperCase());
		emailString.append("</caption>");
		emailString.append("<thead style='background-color: #32CD32'>");
		emailString.append("<tr>");
		emailString
				.append("<th style='border: 1px solid black; color: white; padding:10px;font-family:Helvetica, sans-serif;' >FIELDS</th>");
		emailString
				.append("<th style='border:1px solid black; color:  white; padding:10px;font-family:Helvetica, sans-serif;' >COUNTS</th>");
		emailString.append("</tr>");
		emailString.append("</thead>");
		emailString
				.append("<tbody style='background-color : #DCDCDC ; border-color: #517994; color:black ;padding:10px;'>");
		emailString.append("<tr>");
		emailString
				.append("<td style='border:1px solid black; font-family:Helvetica, sans-serif;'>");
		emailString.append("Verbal Screened");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black;' >");
		emailString.append(factTable.getTotalScreenForm());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Chest X-Rays");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getTotalChestXrays());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Verbal Screen Presumptives");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getTotalVerbalScreenPresumptives());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Chest X-Ray Presumptives");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getTotalChestXrayPresumptives());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Samples Collected from Verbal Screen Presumptives");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable
				.getSamplesCollectedVerbalScreenPresumptives());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Samples Collected from CXR Presumptives");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getSamplesCollectedCXRPresumptives());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Accepted Samples");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getAcceptedSamples());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Internal Tests");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getInternalTests());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("External Tests");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getExternalTests());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("GXP Tests Done");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getGXPTestsDone());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("MTB+ Internal");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getMTBpveInternal());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("MTB+/RR+ Internal");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getMTBpveRRpveInternal());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Error Internal");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getErrorInternal());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("No result Internal");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getNoresultInternal());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Invalid Internal");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getInvalidInternal());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Pending Samples");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getPendingSamples());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Clinically Diagnosed");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getClinicallyDiagnosed());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Initiated on Antibiotic");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getInitiatedOnAntibiotic());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("<tr>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append("Initiated on TB Tx");
		emailString.append("</td>");
		emailString.append("<td style='border:1px solid black'>");
		emailString.append(factTable.getInitiatedOnTBTx());
		emailString.append("</td>");
		emailString.append("</tr>");

		emailString.append("</tbody>");
		emailString.append("</table>");
		emailString.append("</table></body></html>");

		return emailString;
	}

	/************************* Childhood Email Execution *******************/
	
}
