package com.ihsinformatics.gfatmnotifications.email.service;

import com.ihsinformatics.gfatmnotifications.email.controller.NotificationController;
import com.ihsinformatics.gfatmnotifications.email.impl.SMSServiceImpl;

public class SmsServiceInjector implements NotificationInjector {

	@Override
	public ConsumerService getConsumer() {
		NotificationController app = new NotificationController();
		app.setService(new SMSServiceImpl());
		return app;
	}

}
