package com.ihsinformatics.gfatmnotifications.email.service;

public interface ConsumerService {

	public boolean getConnection(String requiredConnection);

	void process();

}
