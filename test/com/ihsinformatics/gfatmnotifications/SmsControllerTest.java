/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.util.Date;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.controllers.SmsController;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SmsControllerTest {

	final String	SERVER_ADDRESS	= "http://202.125.133.156/ihs/api/send_sms/";
	final String	API_KEY			= "aWhzc21zOnVsNjJ6eDM=";
	SmsController	smsController;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		smsController = new SmsController(SERVER_ADDRESS, API_KEY, true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.controllers.SmsController#createSms(java.lang.String, java.lang.String, java.util.Date, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testCreateSms() {
		String sendTo = "03222808980";
		String message = "Bhatti bhatti bhatti";
		Date sendOn = new Date();
		System.out.println(sendOn);
		String projectId = "TEST";
		String additionalInfo = null;
		smsController = new SmsController(SERVER_ADDRESS, API_KEY, false);
		try {
			String response = smsController.createSms(sendTo, message, sendOn,
					projectId, additionalInfo);
			JSONObject responseObj = new JSONObject(response);
			Integer error = responseObj.getInt("error");
			Integer errorCode = responseObj.getInt("code");
			String responseMessage = responseObj.getString("message");
			Assert.assertTrue("Error (" + errorCode + "): " + responseMessage,
					error == 0);
			System.out.println(response);
		} catch (Exception e) {
			Assert.fail("Exception: " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.gfatmnotifications.controllers.SmsController#postSecure(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testCreateSmsSecure() {
		String sendTo = "03222808980";
		String message = "Testing the API";
		Date sendOn = new Date();
		String projectId = "TEST";
		String additionalInfo = null;
		try {
			smsController = new SmsController(SERVER_ADDRESS, API_KEY, true);
			String response = smsController.createSms(sendTo, message, sendOn,
					projectId, additionalInfo);
			JSONObject responseObj = new JSONObject(response);
			Integer error = responseObj.getInt("error");
			Integer errorCode = responseObj.getInt("code");
			String responseMessage = responseObj.getString("message");
			Assert.assertTrue("Error (" + errorCode + "): " + responseMessage,
					error == 0);
			System.out.println(response);
		} catch (Exception e) {
			Assert.fail("Exception: " + e.getMessage());
		}
	}
}
