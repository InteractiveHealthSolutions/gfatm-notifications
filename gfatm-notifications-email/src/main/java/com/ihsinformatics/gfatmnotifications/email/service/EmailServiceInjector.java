package com.ihsinformatics.gfatmnotifications.email.service;

import com.ihsinformatics.gfatmnotifications.email.controller.NotificationController;
import com.ihsinformatics.gfatmnotifications.email.impl.EmailServiceImpl;

public class EmailServiceInjector implements NotificationInjector {

	@Override
	public ConsumerService getConsumer() {
		NotificationController app = new NotificationController();
		app.setService(new EmailServiceImpl());
		return app;
	}

}
