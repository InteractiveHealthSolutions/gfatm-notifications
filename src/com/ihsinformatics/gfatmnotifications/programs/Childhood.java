package com.ihsinformatics.gfatmnotifications.programs;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import com.ihsinformatics.gfatmnotifications.Interface.Isms;
import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.util.DateTimeUtil;

public class Childhood implements Isms {

	private SmsController	smsController;
	private OpenMrsUtil		openMrsUtil;

	@Override
	public void initializeProperties(OpenMrsUtil openMrsUtil,
			SmsController smsController) {
		this.openMrsUtil = openMrsUtil;
		this.smsController = smsController;
	}

	@Override
	public void execute(List<Encounter> encounters) {

		for (Encounter encounter : encounters) {
			Map<String, Object> observations = openMrsUtil
					.getEncounterObservations(encounter);
			;
			encounter.setObservations(observations);
			switch (encounter.getEncounterType()) {
			// ...
				default:
			}
		}
	}
	
	// TODO:complete
		@SuppressWarnings("deprecation")
		public boolean sendTreatmentInitiationSmsChildhood(Encounter encounter) {

			Map<String, Object> observation = encounter.getObservations();
			System.out.print(observation.toString());
			String rtnVisitDate = observation.get("return_visit_date").toString()
					.toUpperCase();

			String iptAcceptance = observation.get("IPT_acceptance").toString()
					.toUpperCase();

			Patient patient = openMrsUtil.getPatientByIdentifier(encounter
					.getIdentifier());

			/****************** conditions *************************************/
			// First Condition
			if (rtnVisitDate.equals(null) || patient.isDead()
					|| !iptAcceptance.equals("YES")) {
				return false;
			}

			Date returnVisitDate;

			// String sendTo = encounter.getPatientContact();
			// System.out.println(sendTo);
			try {
				returnVisitDate = DateTimeUtil.getDateFromString(rtnVisitDate,
						DateTimeUtil.SQL_DATETIME);

				LocalDate duelocalDate = new LocalDate(returnVisitDate);
				duelocalDate.minusDays(1);
				// Date dueDate = duelocalDate.toDate();

				/*
				 * Calendar dueDate = Calendar.getInstance();
				 * dueDate.setTime(returnVisitDate); dueDate.set(Calendar.DATE,
				 * dueDate.get(Calendar.DATE) - 1);
				 */

				// Map<String, Object> observations = encounter.getObservations();
				// Location referralLocation = null;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;

		}
	

}
