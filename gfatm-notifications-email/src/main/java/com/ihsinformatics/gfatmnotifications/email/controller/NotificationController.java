package com.ihsinformatics.gfatmnotifications.email.controller;

import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.common.service.NotificationService;
import com.ihsinformatics.gfatmnotifications.email.DatabaseConnection;
import com.ihsinformatics.gfatmnotifications.email.model.Constants;
import com.ihsinformatics.gfatmnotifications.email.service.ConsumerService;

public class NotificationController implements ConsumerService {

	private NotificationService service;

	public void setService(NotificationService service) {
		this.service = service;
	}

	@Override
	public void process() throws JobExecutionException {
		// do some common validation notification validation, manipulation logic
		service.initialize();
		service.execute(null);
	}

	@Override
	public boolean getConnection(String requiredConnection) {

		DatabaseConnection connection = new DatabaseConnection();

		if (requiredConnection.equals(Constants.WAREHOUSE_CONNECTION)) {

			if (!connection.wareHouseConnection()) {
				System.out.println("Failed to connect with warehouse local database. Exiting");
				System.exit(-1);
			}

			return true;
		}
		if (requiredConnection.equals(Constants.OPENMRS_CONNECTION)) {
			if (!connection.openmrsDbConnection()) {
				System.out.println("Failed to connect with local database. Exiting");
				System.exit(-1);
			}
			return true;
		} else {
			System.out.println("Connection Requested String is not match...Exiting");
			System.exit(-1);
			return false;
		}
	}

}
