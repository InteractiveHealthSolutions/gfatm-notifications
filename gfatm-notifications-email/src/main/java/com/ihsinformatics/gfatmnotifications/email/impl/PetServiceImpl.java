package com.ihsinformatics.gfatmnotifications.email.impl;

import java.util.List;

import com.ihsinformatics.gfatmnotifications.email.controller.SmsController;
import com.ihsinformatics.gfatmnotifications.email.model.Encounter;
import com.ihsinformatics.gfatmnotifications.email.service.SmsService;
import com.ihsinformatics.gfatmnotifications.email.util.OpenMrsUtil;

public class PetServiceImpl implements SmsService {

	@Override
	public void initializeProperties(OpenMrsUtil openMrsUtil, SmsController smsController) {

	}

	@Override
	public void execute(List<Encounter> encounters) {

	}

}
