package com.ihsinformatics.gfatmnotifications.service;

import com.ihsinformatics.gfatmnotifications.Implementer.EmailServiceImpl;
import com.ihsinformatics.gfatmnotifications.Interface.IConsumer;
import com.ihsinformatics.gfatmnotifications.controllers.NotificationController;

public class EmailServiceInjector implements NotificationInjector {

	@Override
	public IConsumer getConsumer() {
		NotificationController app = new NotificationController();
		app.setService(new EmailServiceImpl());
		return app;
	}

}
