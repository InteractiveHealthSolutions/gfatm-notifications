package com.ihsinformatics.gfatmnotifications.Interface;

import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;

public interface Iemail {

	public void execute(OpenMrsUtil openMrsUtil);
	public boolean sendEmail(String emailAdress, String message, String subject);
	public void initializeProperties();

}
