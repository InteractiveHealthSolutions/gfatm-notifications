package com.ihsinformatics.gfatmnotifications.program;

import java.util.List;

import com.ihsinformatics.gfatmnotifications.controller.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.service.SmsService;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;

public class PetServiceImpl implements SmsService {

	@Override
	public void initializeProperties(OpenMrsUtil openMrsUtil, SmsController smsController) {

	}

	@Override
	public void execute(List<Encounter> encounters) {

	}

}
