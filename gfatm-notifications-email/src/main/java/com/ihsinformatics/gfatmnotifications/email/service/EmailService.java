package com.ihsinformatics.gfatmnotifications.email.service;

import com.ihsinformatics.gfatmnotifications.email.util.CustomOpenMrsUtil;

public interface EmailService {

	public void execute(CustomOpenMrsUtil openMrsUtil);

	public boolean sendEmail(String emailAdress, String message, String subject);

	public void initializeProperties();

}
