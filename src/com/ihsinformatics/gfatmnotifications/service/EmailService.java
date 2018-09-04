package com.ihsinformatics.gfatmnotifications.service;

import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;

public interface EmailService {

	public void execute(OpenMrsUtil openMrsUtil);

	public boolean sendEmail(String emailAdress, String message, String subject);

	public void initializeProperties();

}
