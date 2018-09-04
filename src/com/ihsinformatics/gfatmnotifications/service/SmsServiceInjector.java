package com.ihsinformatics.gfatmnotifications.service;

import com.ihsinformatics.gfatmnotifications.Implementer.SMSServiceImpl;
import com.ihsinformatics.gfatmnotifications.controller.NotificationController;

public class SmsServiceInjector implements NotificationInjector {

	@Override
	public ConsumerService getConsumer() {
		NotificationController app = new NotificationController();
		app.setService(new SMSServiceImpl());
		return app;
	}

}
