package com.ihsinformatics.gfatmnotifications.email.service;

import org.quartz.JobExecutionException;

public interface ConsumerService {

	public boolean getConnection(String requiredConnection);

	void process() throws JobExecutionException;

}
