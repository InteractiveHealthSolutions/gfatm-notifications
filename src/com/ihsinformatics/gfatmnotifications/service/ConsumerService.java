package com.ihsinformatics.gfatmnotifications.service;

public interface ConsumerService {

	public boolean getConnection(String requiredConnection);

	void process();

}
