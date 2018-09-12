package com.ihsinformatics.gfatmnotifications.email.service;

import org.quartz.JobExecutionException;

public interface ConsumerService {

	void process() throws JobExecutionException;

}
