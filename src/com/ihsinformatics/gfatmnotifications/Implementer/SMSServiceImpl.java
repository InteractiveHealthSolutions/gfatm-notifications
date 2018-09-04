package com.ihsinformatics.gfatmnotifications.Implementer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.controller.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.program.ChildhoodServiceImpl;
import com.ihsinformatics.gfatmnotifications.program.FastServiceImpl;
import com.ihsinformatics.gfatmnotifications.program.PetServiceImpl;
import com.ihsinformatics.gfatmnotifications.service.SmsService;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;
import com.ihsinformatics.gfatmnotifications.util.ValidationUtil;
import com.ihsinformatics.util.DatabaseUtil;
import com.mchange.v2.cfg.PropertiesConfigSource.Parse;

public class SMSServiceImpl implements NotificationService {

	private DateTime dateFrom;
	private DateTime dateTo;
	private DatabaseUtil openmrsDb;
	private OpenMrsUtil openmrsUtile;
	private SmsService sms;
	private SmsController smsController;

	public SMSServiceImpl() {

		dateFrom = new DateTime();
		dateTo = dateFrom.minusHours(Constants.SMS_SCHEDULE_INTERVAL_IN_HOURS);
		smsController = new SmsController(Constants.SMS_SERVER_ADDRESS, Constants.SMS_API_KEY, Constants.SMS_USE_SSL);
	}

	@Override
	public void run() {

		System.out.println("Values : ");
		{
			List<Encounter> fastEncounters = executeFastSms(dateFrom, dateTo);
			sms = new FastServiceImpl();
			sms.initializeProperties(openmrsUtile, smsController);
			sms.execute(fastEncounters);
		}
		/*
		 * // Childhood sms {
		 * 
		 * List<Encounter> childhoodEncounters = executeChildhoodSms(dateFrom, dateTo);
		 * sms = new Childhood(); sms.initializeProperties(openmrsUtile, smsController);
		 * sms.execute(childhoodEncounters);
		 * 
		 * } // Pet sms { List<Encounter> petEncounters = executePetSms(dateFrom,
		 * dateTo); sms = new Pet(); sms.initializeProperties(openmrsUtile,
		 * smsController); sms.execute(petEncounters); }
		 */
	}

	@Override
	public void loader() {

		openmrsDb = UtilityCollection.getInstance().getLocalDb();
		openmrsUtile = new OpenMrsUtil(openmrsDb);
		openmrsUtile.loadLocations();
		openmrsUtile.loadPatients();
		openmrsUtile.loadUsers();
		openmrsUtile.loadEncounterTypes();
	}

	public List<Encounter> executeFastSms(DateTime dateFrom, DateTime dateTo) {

		List<Encounter> encounters = new ArrayList<Encounter>();
		for (int type : Constants.FAST_ENCOUNTER_TYPE_IDS) {

			List<Encounter> temp = openmrsUtile.getEncounters(dateFrom, dateTo, type);
			encounters.addAll(temp);
		}
		// Some encounters will be removed
		List<Encounter> toDelete = new ArrayList<Encounter>();
		for (Encounter encounter : encounters) {
			// Remove encounters with missing or fake mobile numbers
			System.out.println(encounter.getPatientContact());
			if (encounter.getPatientContact() == null) {
				toDelete.add(encounter);
			} else if (!ValidationUtil.isValidContactNumber(encounter.getPatientContact())) {
				toDelete.add(encounter);
			}
		}

		encounters.removeAll(toDelete);

		return encounters;
	}

	public List<Encounter> executeChildhoodSms(DateTime dateFrom, DateTime dateTo) {

		List<Encounter> encounters = new ArrayList<Encounter>();
		for (int type : Constants.CHILDHOOD_TB_ENCOUNTER_TYPE_IDS) {

			List<Encounter> temp = openmrsUtile.getEncounters(dateFrom, dateTo, type);
			encounters.addAll(temp);
		}
		// Some encounters will be removed
		List<Encounter> toDelete = new ArrayList<Encounter>();
		for (Encounter encounter : encounters) {
			// Remove encounters with missing or fake mobile numbers
			System.out.println(encounter.getPatientContact());
			if (encounter.getPatientContact() == null) {
				toDelete.add(encounter);
			} else if (!ValidationUtil.isValidContactNumber(encounter.getPatientContact())) {
				toDelete.add(encounter);
			}
		}

		encounters.removeAll(toDelete);
		return encounters;
	}

	public List<Encounter> executePetSms(DateTime dateFrom, DateTime dateTo) {
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (int type : Constants.PET_ENCOUNTER_TYPE_IDS) {
			List<Encounter> temp = openmrsUtile.getEncounters(dateFrom, dateTo, type);
			encounters.addAll(temp);
		}
		// Some encounters will be removed
		List<Encounter> toDelete = new ArrayList<Encounter>();
		for (Encounter encounter : encounters) {
			// Remove encounters with missing or fake mobile numbers
			System.out.println(encounter.getPatientContact());
			if (encounter.getPatientContact() == null) {
				toDelete.add(encounter);
			} else if (!ValidationUtil.isValidContactNumber(encounter.getPatientContact())) {
				toDelete.add(encounter);
			}
		}

		encounters.removeAll(toDelete);

		return encounters;
	}

	public boolean contains(final int[] array, final String key) {
		int keyVal = Integer.getInteger(key);
		return ArrayUtils.contains(array, keyVal);
	}

}
