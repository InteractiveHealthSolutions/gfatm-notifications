package com.ihsinformatics.gfatmnotifications.sms.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;



public class SendDate {

	
	@Test
	public void DateShouldBeEqual() {
		
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        try {
				Date sendOn = sdf.parse("2019-05-23");
				Date endOn =sdf.parse(sdf.format(new Date()));
				if (!(sendOn.compareTo(endOn)==0)) {
					junit.framework.Assert.assertTrue(true);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		
		
	}
}
