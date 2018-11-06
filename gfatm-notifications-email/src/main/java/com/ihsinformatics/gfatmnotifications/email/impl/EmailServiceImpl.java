package com.ihsinformatics.gfatmnotifications.email.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.service.NotificationService;
import com.ihsinformatics.gfatmnotifications.email.job.CallCenterEmailJob;
import com.ihsinformatics.gfatmnotifications.email.job.GfatmEmailJob;
import com.ihsinformatics.gfatmnotifications.email.model.Constants;
import com.ihsinformatics.gfatmnotifications.email.service.EmailService;
import com.ihsinformatics.gfatmnotifications.email.util.CustomGfatmDatabaseUtil;
import com.ihsinformatics.util.DatabaseUtil;

public class EmailServiceImpl implements NotificationService {

	private CustomGfatmDatabaseUtil openMrsUtil;
	private EmailService emailJob;
	private DateTime startVisitDate, endVisitDate;
	public SimpleDateFormat DATE_FORMATWH = new SimpleDateFormat("yyyy-MM-dd");
	private DatabaseUtil dbUtil;
	public EmailServiceImpl() {
		// Reload contacts
		dbUtil = Context.getDwDb();
		Context.loadContacts(dbUtil);
		startVisitDate = new DateTime();
		endVisitDate = startVisitDate.plusDays(Constants.NUMBERDAYS);
		openMrsUtil = new CustomGfatmDatabaseUtil();
		openMrsUtil.getPatientScheduledForVisit(DATE_FORMATWH.format(startVisitDate.toDate()),
				DATE_FORMATWH.format(endVisitDate.toDate()),dbUtil);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			// Module One: Gfatm email Notification (daily)
			System.out.println("/*************Start Gfatm Email Notification ... **************/");
			emailJob = new GfatmEmailJob();
			emailJob.initializeProperties();
			emailJob.execute(new CustomGfatmDatabaseUtil());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// Module Two: Call Center Email Notification (daily)
			System.out.println("/*************Start Call Center Email Notification ... **************/");
			emailJob = new CallCenterEmailJob();
			emailJob.initializeProperties();
			emailJob.execute(new CustomGfatmDatabaseUtil());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String sendNotification(String adressTo, String message, String subject, Date sendOn) {
		return null;
	}

}
