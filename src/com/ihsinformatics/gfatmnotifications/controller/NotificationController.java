package com.ihsinformatics.gfatmnotifications.controller;

import com.ihsinformatics.gfatmnotifications.DatabaseConnection;
import com.ihsinformatics.gfatmnotifications.Implementer.NotificationService;
import com.ihsinformatics.gfatmnotifications.model.Constants;
import com.ihsinformatics.gfatmnotifications.service.ConsumerService;

public class NotificationController implements ConsumerService {

	private NotificationService service;

	public void setService(NotificationService service) {
		this.service = service;
	}

	@Override
	public void process() {
		// do some common validatin notification validation, manipulation logic
		service.loader();
		service.run();
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
