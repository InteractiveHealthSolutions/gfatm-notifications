package com.ihsinformatics.gfatmnotifications.service;

import com.ihsinformatics.gfatmnotifications.Implementer.EmailServiceImpl;
import com.ihsinformatics.gfatmnotifications.controller.NotificationController;

public class EmailServiceInjector implements NotificationInjector {

	@Override
	public ConsumerService getConsumer() {
		NotificationController app = new NotificationController();
		app.setService(new EmailServiceImpl());
		return app;
	}

}
