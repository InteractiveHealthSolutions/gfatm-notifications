package com.ihsinformatics.gfatmnotifications.service;

import com.ihsinformatics.gfatmnotifications.Implementer.SMSServiceImpl;
import com.ihsinformatics.gfatmnotifications.Interface.IConsumer;
import com.ihsinformatics.gfatmnotifications.controllers.NotificationController;

public class SmsServiceInjector implements NotificationInjector {

	
	@Override
	public IConsumer getConsumer() {
		NotificationController app = new NotificationController();
		app.setService(new SMSServiceImpl());
		return app;
	}

}
