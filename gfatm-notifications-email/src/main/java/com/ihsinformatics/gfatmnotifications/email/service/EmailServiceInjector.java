package com.ihsinformatics.gfatmnotifications.email.service;

import com.ihsinformatics.gfatmnotifications.common.service.NotificationService;
import com.ihsinformatics.gfatmnotifications.email.controller.NotificationController;
import com.ihsinformatics.gfatmnotifications.email.impl.EmailServiceImpl;

public class EmailServiceInjector implements NotificationInjector {

	@Override
	public ConsumerService getConsumer() {
		NotificationController app = new NotificationController();
		NotificationService service = new EmailServiceImpl();
		app.setService(service);
		return app;
	}

}
