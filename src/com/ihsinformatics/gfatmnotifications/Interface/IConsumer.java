package com.ihsinformatics.gfatmnotifications.Interface;

public interface IConsumer {

	public boolean getConnection(String requiredConnection);

	void process();

}
