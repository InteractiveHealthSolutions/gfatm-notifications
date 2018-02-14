package com.ihsinformatics.gfatmnotifications.Implementer;

import java.util.Calendar;

import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.Interface.Iemail;
import com.ihsinformatics.gfatmnotifications.jobs.CallCenterEmailJob;
import com.ihsinformatics.gfatmnotifications.jobs.GfatmEmailJob;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;

public class EmailServiceImpl implements NotificationService {

	private Calendar	calendar	= Calendar.getInstance();
	private OpenMrsUtil	openMrsUtil;
	private Iemail		email;
	private DateTime	startVisitDate, endVisitDate;

	public EmailServiceImpl() {
		startVisitDate = new DateTime();
		endVisitDate = startVisitDate.plusDays(Constants.NUMBERDAYS);
	}

	@Override
	public void run() {
		// Gfatm email Notification (daily)
		email = new GfatmEmailJob();
		email.execute(new OpenMrsUtil(UtilityCollection.getWarehouseDb()));
		// Call Center Email Notification (Bi-week -> MONDAY and THURSDAY )
		if (getBiWeekCondition()) {
			email = new CallCenterEmailJob();
			email.execute(new OpenMrsUtil(UtilityCollection.getWarehouseDb()));
		}
	}

	public boolean getBiWeekCondition() {

		boolean isExecutionDay = false;
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				isExecutionDay = true;
				break;
			case Calendar.THURSDAY:
				isExecutionDay = true;
				break;
			case Calendar.TUESDAY:
				isExecutionDay = true;
				break;
		}

		return isExecutionDay;
	}

	@Override
	public void loader() {

		openMrsUtil = new OpenMrsUtil(UtilityCollection.getWarehouseDb());
		openMrsUtil.LoadAllUsersEmail();
		if (getBiWeekCondition()) {
			openMrsUtil.getPatientScheduledForVisit(
					Constants.DATE_FORMATWH.format(startVisitDate.toDate()),
					Constants.DATE_FORMATWH.format(endVisitDate.toDate()));
		}

	}

}
