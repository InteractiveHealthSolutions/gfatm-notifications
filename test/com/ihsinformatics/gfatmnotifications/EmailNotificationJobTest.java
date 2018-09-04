package com.ihsinformatics.gfatmnotifications;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.gfatmnotifications.controller.EmailController;
import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.util.OpenMrsUtil;
import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;
import com.ihsinformatics.util.DatabaseUtil;

@SuppressWarnings({ "deprecation" })
public class EmailNotificationJobTest {
	OpenMrsUtil	openMrsUtil;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		// make a database connection
		DatabaseConnection con = new DatabaseConnection();
		if (con.openmrsDbConnection()) {
			System.out.println("Openmrs Database Connection");
		}
		// check the data warehouse connection
		if (con.wareHouseConnection()) {
			System.out.println("warehouse Database Connection");
		}

	}

	// check the database connection
	@Test
	public void connectionTest() {

		DatabaseUtil localDb = UtilityCollection.getInstance().getWarehouseDb();
		Assert.assertTrue(localDb.tryConnection());
	}

	@Test
	public void checkTheFastFactTable() {

		System.out.println("start the execution");
		openMrsUtil = new OpenMrsUtil(UtilityCollection.getInstance().getWarehouseDb());
		openMrsUtil.LoadAllUsersEmail();
		// check the email agains the location id
		assertNotNull(openMrsUtil.getEmailByLocationId(2));
	}

	@Test
	public void sendEmail() {
		System.out.println("sending email");
		openMrsUtil = new OpenMrsUtil(UtilityCollection.getInstance().getWarehouseDb());
		openMrsUtil.LoadAllUsersEmail();
		Email email = openMrsUtil.getEmailByLocationId(2);
		System.out.println("" + email.getEmailAdress());

		EmailController emailController = new EmailController();
		String fromEmail = UtilityCollection.getInstance().getProps().getProperty(
				"mail.user.username");
		Assert.assertTrue(emailController.sendEmailWithHtml(
				"shujaat.ali@ihsinformatics.com", "Test Subject",
				getDynamicEmailFormat(), fromEmail));

	}

	public String getDynamicEmailFormat() {

		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("Verbal Screened", "Amit");
		mapping.put("Chest X-Rays", "Amit");
		mapping.put("Verbal Screen Presumptives", "Amit");
		mapping.put("Chest X-Ray Presumptives", "Amit");
		mapping.put("Samples Collected from Verbal Screen Presumptives", "Amit");
		mapping.put("GXP Tests Done", "Amit");
		mapping.put("# Internal Tests", "Amit");
		mapping.put("# External Tests", "Amit");
		mapping.put("# External Tests", "Amit");
		return tableFormate(mapping);

	}

	@SuppressWarnings("rawtypes")
	public String tableFormate(Map<String, String> mapping) {

		StringBuilder buf = new StringBuilder();
		buf.append("<!DOCTYPE html>");
		buf.append("<html>"
				+ "<body>"
				+ "<table style='font-family: 'Trebuchet MS', Arial, Helvetica, sans-serif; border-collapse: collapse; width: 100%;'>"
				+ "<caption style='padding: 8px; padding-top: 12px; padding-bottom: 12px;font-size: 130%;'><code>Location One</code></caption>"
				+ "<tr>"
				+ "<th style = ' background-color: #110934;color: white;  border: 1px solid #DDDDDD; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Forms</th>"
				+ "<th style = ' background-color: #110934;color: white;  border: 1px solid #DDDDDD; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Count</th>"
				+ "</tr>");
		for (Map.Entry m : mapping.entrySet()) {

			buf.append(
					"<tr><td style = 'border: 1px solid #DDDDDD;padding: 8px;'>")
					.append(m.getKey())
					.append("</td><td style = 'border: 1px solid #DDDDDD;padding: 8px;'>")
					.append(m.getValue()).append("</td></tr>");
		}
		buf.append("</table>" + "</body>" + "</html>");
		String html = buf.toString();

		return html;

	}
}
