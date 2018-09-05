/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */
package com.ihsinformatics.gfatmnotifications.email;

import java.util.Date;
import java.util.logging.Logger;

import com.ihsinformatics.gfatmnotifications.email.model.Constants;
import com.ihsinformatics.gfatmnotifications.email.service.ConsumerService;
import com.ihsinformatics.gfatmnotifications.email.service.EmailServiceInjector;
import com.ihsinformatics.gfatmnotifications.email.service.NotificationInjector;

/**
 * @author owais.hussain@ihsinformatics.com
 * @author Shujaat
 *
 */

public class GfatmNotificationsMain {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static NotificationInjector injector;
	private static ConsumerService consumer;

	/**
	 * @param args
	 * @throws InputRequiredException
	 * @throws DatabaseUpdateException
	 * @throws ModuleMustStartException
	 */
	public static void main(String[] args) {
		try {
			// Email Notification
			injector = new EmailServiceInjector();
			consumer = injector.getConsumer();
			consumer.getConnection(Constants.WAREHOUSE_CONNECTION);
			consumer.process();
			log.info("Email Notification execution is complete on : " + new Date());
			System.exit(0);
		} catch (Exception e) {
			log.info("Exception : " + e.getMessage());
			System.exit(-1); // this will be remove
		}
	}

	public GfatmNotificationsMain() {
		DatabaseConnection connection = new DatabaseConnection();
		/*
		 * if (!connection.openmrsDbConnection()) {
		 * System.out.println("Failed to connect with local database. Exiting"); }
		 */
		if (!connection.wareHouseConnection()) {
			System.out.println("Failed to connect with warehouse local database. Exiting");
			System.exit(-1);
		}

	}
}
