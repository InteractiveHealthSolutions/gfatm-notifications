package com.ihsinformatics.gfatmnotifications.email.service;

import java.util.List;

import com.ihsinformatics.gfatmnotifications.email.controller.SmsController;
import com.ihsinformatics.gfatmnotifications.email.model.Encounter;
import com.ihsinformatics.gfatmnotifications.email.util.OpenMrsUtil;

public interface SmsService {

	public void initializeProperties(OpenMrsUtil openMrsUtil, SmsController smsController);

	public void execute(List<Encounter> encounters);
}
