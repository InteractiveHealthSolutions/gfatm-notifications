package com.ihsinformatics.gfatmnotifications.Implementer;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.Interface.Iemail;
import com.ihsinformatics.gfatmnotifications.jobs.CallCenterEmailJob;
import com.ihsinformatics.gfatmnotifications.jobs.GfatmEmailJob;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;

public class EmailServiceImpl implements NotificationService {

	private OpenMrsUtil		openMrsUtil;
	private Iemail			email;
	private DateTime		startVisitDate, endVisitDate;
	public SimpleDateFormat	DATE_FORMATWH	= new SimpleDateFormat("yyyy-MM-dd");

	public EmailServiceImpl() {
		startVisitDate = new DateTime();
		endVisitDate = startVisitDate.plusDays(Constants.NUMBERDAYS);
	}

	@Override
	public void run() {

		try {
			// Module One: Gfatm email Notification (daily)
		    System.out.println("/*************Start Gfatm Email Notification ... **************/");
			email = new GfatmEmailJob();
			email.initializeProperties();
			email.execute(new OpenMrsUtil(UtilityCollection.getInstance()
					.getWarehouseDb()));

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
			email.execute(new OpenMrsUtil(UtilityCollection.getInstance()
					.getWarehouseDb()));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void loader() {
		System.out
				.println("/*************Loading Email Adress and Locations ... **************/");
		openMrsUtil = new OpenMrsUtil(UtilityCollection.getInstance()
				.getWarehouseDb());
		openMrsUtil.LoadAllUsersEmail();
		openMrsUtil.getPatientScheduledForVisit(
				DATE_FORMATWH.format(startVisitDate.toDate()),
				DATE_FORMATWH.format(endVisitDate.toDate()));
	}

}
