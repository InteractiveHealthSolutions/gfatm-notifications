package com.ihsinformatics.gfatmnotifications.email.service;

import com.ihsinformatics.gfatmnotifications.email.util.CustomGfatmDatabaseUtil;

public interface EmailService {

	public void execute(CustomGfatmDatabaseUtil openMrsUtil);

	public boolean sendEmail(String emailAdress, String message, String subject);

	public void initializeProperties();

}
