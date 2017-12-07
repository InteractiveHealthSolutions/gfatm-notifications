package com.ihsinformatics.gfatmnotifications;

import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.ihs.emailer.EmailEngine;


public class EmailController {
	private static final Logger log = Logger.getLogger(Class.class.getName());
	
	 public EmailController() {
	}
	
	/**
	 * with html message email send.
	 * 
	 * @param recipient
	 * @param subject
	 * @param text
	 * @param from
	 * @return
	 */
	public boolean sendEmailWithHtml(String recipientList, String subject,
			String message, String from) {
	
		boolean isSent;
		String[] recipient = {recipientList};
	
		try {
			isSent = EmailEngine.getInstance().postHtmlMail(recipient,
					subject, message, from);
		} catch (MessagingException e) {
			e.printStackTrace();
			log.warning(""+e.getMessage());
			return false;
		}
		return isSent;
	}
	
    /**
     * This method send email with plan text
     * 
     * @param recipientList
     * @param subject
     * @param message
     * @param from
     * @return
     */
	public boolean sendEmail(String recipientList, String subject,
			String message, String from) {
		
		boolean isSent;
		String[] recipient = {recipientList};
	
		try {
			isSent = EmailEngine.getInstance().postSimpleMail(recipient,
					subject, message, from);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		return isSent;
	}

	public boolean sendEmailWithAttachment() {

		//EmailEngine.getInstance().postEmailWithAttachment(recipients, subject, htmlmsg, from, bytes, filename, attachmentType);
		
		return true;
	}


}
