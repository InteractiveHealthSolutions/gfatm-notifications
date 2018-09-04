package com.ihsinformatics.gfatmnotifications.controllers;

import com.ihsinformatics.gfatmnotifications.Implementer.NotificationService;
import com.ihsinformatics.gfatmnotifications.Interface.IConsumer;
import com.ihsinformatics.gfatmnotifications.databaseconnections.Connections;
import com.ihsinformatics.gfatmnotifications.model.Constants;

public class NotificationController implements IConsumer {

	private NotificationService	service;

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

		Connections connection = new Connections();

		if (requiredConnection.equals(Constants.WAREHOUSE_CONNECTION)) {

			if (!connection.wareHouseConnection()) {
				System.out
						.println("Failed to connect with warehouse local database. Exiting");
				System.exit(-1);
			}

			return true;
		}
		if (requiredConnection.equals(Constants.OPENMRS_CONNECTION)) {
			if (!connection.openmrsDbConnection()) {
				System.out
						.println("Failed to connect with local database. Exiting");
				System.exit(-1);
			}
			return true;
		} else {
			System.out
					.println("Connection Requested String is not match...Exiting");
			System.exit(-1);
			return false;
		}
	}

}
