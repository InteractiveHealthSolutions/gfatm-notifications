package com.ihsinformatics.gfatmnotifications.programs;

import java.util.List;

import com.ihsinformatics.gfatmnotifications.Interface.Isms;
import com.ihsinformatics.gfatmnotifications.controllers.SmsController;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;

public class Pet implements Isms {

	@Override
	public void initializeProperties(OpenMrsUtil openMrsUtil,
			SmsController smsController) {
		
	}

	@Override
	public void execute(List<Encounter> encounters) {
		
	}

}
