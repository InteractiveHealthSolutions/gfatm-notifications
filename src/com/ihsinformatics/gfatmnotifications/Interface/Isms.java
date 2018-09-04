package com.ihsinformatics.gfatmnotifications.Interface;

import java.util.List;

import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;

public interface Isms {
	
	public void initializeProperties(OpenMrsUtil openMrsUtil,SmsController smsController);
	public void execute(List<Encounter>encounters);
}
