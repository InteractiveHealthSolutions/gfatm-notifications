package com.ihsinformatics.gfatmnotifications.email.controller;

import org.quartz.JobExecutionException;

import com.ihsinformatics.gfatmnotifications.common.service.NotificationService;
import com.ihsinformatics.gfatmnotifications.email.service.ConsumerService;

public class NotificationController implements ConsumerService {

	private NotificationService service;

	public void setService(NotificationService service) {
		this.service = service;
	}

	@Override
	public void process() throws JobExecutionException {
		service.execute(null);
	}
}
