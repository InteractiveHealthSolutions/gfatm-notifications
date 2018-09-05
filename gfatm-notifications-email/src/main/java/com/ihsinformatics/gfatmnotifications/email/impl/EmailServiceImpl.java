package com.ihsinformatics.gfatmnotifications.email.impl;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.common.service.NotificationService;
import com.ihsinformatics.gfatmnotifications.common.service.UtilityCollection;
import com.ihsinformatics.gfatmnotifications.email.job.CallCenterEmailJob;
import com.ihsinformatics.gfatmnotifications.email.job.GfatmEmailJob;
import com.ihsinformatics.gfatmnotifications.email.model.Constants;
import com.ihsinformatics.gfatmnotifications.email.service.EmailService;
import com.ihsinformatics.gfatmnotifications.email.util.CustomOpenMrsUtil;

public class EmailServiceImpl implements NotificationService {

	private CustomOpenMrsUtil openMrsUtil;
	private EmailService email;
	private DateTime startVisitDate, endVisitDate;
	public SimpleDateFormat DATE_FORMATWH = new SimpleDateFormat("yyyy-MM-dd");

	public EmailServiceImpl() {
		startVisitDate = new DateTime();
		endVisitDate = startVisitDate.plusDays(Constants.NUMBERDAYS);
	}

	@Override
	public void initialize() {
		System.out.println("/*************Loading Email Adress and Locations ... **************/");
		openMrsUtil = new CustomOpenMrsUtil(UtilityCollection.getInstance().getWarehouseDb());
		openMrsUtil.LoadAllUsersContact();
		openMrsUtil.getPatientScheduledForVisit(DATE_FORMATWH.format(startVisitDate.toDate()),
				DATE_FORMATWH.format(endVisitDate.toDate()));
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			// Module One: Gfatm email Notification (daily)
			System.out.println("/*************Start Gfatm Email Notification ... **************/");
			email = new GfatmEmailJob();
			email.initializeProperties();
			email.execute(new CustomOpenMrsUtil(UtilityCollection.getInstance().getWarehouseDb()));
		} catch (Exception e) {
			e.printStackTrace();
			// if the emailjob class thrown any exception then we handle this
			// except right here so our below process not stop...
		}
		try {
			// Module Two: Call Center Email Notification (daily)
			System.out.println("/*************Start Call Center Email Notification ... **************/");
			email = new CallCenterEmailJob();
			email.initializeProperties();
			email.execute(new CustomOpenMrsUtil(UtilityCollection.getInstance().getWarehouseDb()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean sendNotification(String adressTo, String message, String subject) {
		return false;
	}

}
