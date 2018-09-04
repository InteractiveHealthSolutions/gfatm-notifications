package com.ihsinformatics.gfatmnotifications.service;

import java.util.List;

import com.ihsinformatics.gfatmnotifications.controller.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;

public interface SmsService {

	public void initializeProperties(OpenMrsUtil openMrsUtil, SmsController smsController);

	public void execute(List<Encounter> encounters);
}
