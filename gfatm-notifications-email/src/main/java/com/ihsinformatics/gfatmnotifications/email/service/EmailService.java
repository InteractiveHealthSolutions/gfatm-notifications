package com.ihsinformatics.gfatmnotifications.email.service;

import com.ihsinformatics.gfatmnotifications.email.util.OpenMrsUtil;

public interface EmailService {

	public void execute(OpenMrsUtil openMrsUtil);

	public boolean sendEmail(String emailAdress, String message, String subject);

	public void initializeProperties();

}
