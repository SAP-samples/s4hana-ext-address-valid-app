package com.sap.cloud.s4hana.eventing.sendmail;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;
import com.sap.cloud.sdk.cloudplatform.connectivity.GenericDestination;

/**
 * Contains convenience methods to construct and send an email using JavaMail
 * API.
 *
 * @see Session
 * @see MimeMessage
 */
public class Email {
	
	private static final Logger log = LoggerFactory.getLogger(Email.class);

	/**
	 * Name of the MAIL destination property that contains "from" email address
	 * 
	 * @see GenericDestination#getPropertiesByName()
	 */
	public static final String DESTINATION_PROPERTY_FROM = "mail.from";
	
	private final Session session;
	
	private final MimeMessage message;
	
	public Email(Session session) {
		this.session = session;
		this.message = new MimeMessage(session);
		
		try {
			from(session.getProperty(DESTINATION_PROPERTY_FROM));
		} catch (Exception e) {
			log.warn("Exception when setting the default email FROM property. The reason could be that "
					+ DESTINATION_PROPERTY_FROM + " property is not specified in the destination or not correct", e);
		}
	}
	
	public static Email forSession(Session session) {
		return new Email(session);
	}

	/**
	 * Sets {@code FROM} header to the email message
	 * 
	 * @param address
	 *            Sender email address in the following format:<br>
	 *            {@code Optional Recipient Name <mandatory@email.address>}
	 */
	public Email from(String address) throws SAPMailingException {
		try {
			InternetAddress fromAddress = parseEmailAddress(address);
			message.setFrom(fromAddress);
		} catch (AddressException e) {
			throw error("From address \"" + address + "\" is not a valid email address", e);
		} catch (MessagingException e) {
			throw error("Error while setting the from email address \"" + address + "\" to the message", e);
		}
		
		return this;
	}
	
	/**
	 * Set recipients' email addresses to the email message
	 * 
	 * @param addresses
	 *            A list of one or more email addresses in the following
	 *            format:<br>
	 *            {@code Optional Recipient Name <mandatory@email.address>}
	 */
	public Email to(String addresses) throws SAPMailingException {
		try {
			message.setRecipients(RecipientType.TO, parseEmailAddresses(addresses));
		} catch (AddressException e) {
			throw error("Recipient addresses \"" + addresses + "\" contain not a valid email address or empty", e);
		} catch (MessagingException e) {
			throw error("Error while setting the recipient \"" + addresses + "\" email addresses to the message", e);
		}
		
		return this;
	}
	
	/**
	 * Set email subject
	 * 
	 * @param subject
	 *            Subject string in UTF-8 encoding
	 */
	public Email subject(String subject) throws SAPMailingException {
		try {
			message.setSubject(subject, "UTF-8");
		} catch (MessagingException e) {
			throw error("Error while setting subject \"" + subject + "\" to email", e);
		}
		
		return this;
	}
	
	/**
	 * Set email message body in HTML format
	 * 
	 * @param html
	 *            Email message body in HTML format and in UTF-8 encoding
	 */
	public Email body(String html) throws SAPMailingException {
		try {
			message.setText(html, "UTF-8", "html");
		} catch (MessagingException e) {
			throw error("Error while setting HTML body \"" + html + "\" to email", e);
		}
		
		return this;
	}
	
	@VisibleForTesting
	protected MimeMessage getMimeMessage() {
		return message;
	}
	
	public void send() throws SAPMailingException {
    	final Transport transport;
    	
    	try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			throw error("Mail protocol cannot be obtained for javax.mail.Session", e);
		}
    	
    	try {
			transport.connect();
		} catch (AuthenticationFailedException e) {
			throw error("Authentication error while connecting to the email server", e);
		} catch (MessagingException e) {
			throw error("Exception while connecting to the email server", e);
		}
    	
		try {
			transport.sendMessage(message, message.getAllRecipients());
		} catch (SendFailedException e) {
			throw error("Notification email was not sent because one or more recipient addresses was rejected", e);
		} catch (MessagingException e) {
			throw error("Exception while sending email", e);
		}
	}

	public static InternetAddress parseEmailAddress(final String address) throws AddressException {
		InternetAddress[] emailAddresses = InternetAddress.parse(address);
		
		if (emailAddresses.length != 1) {
			throw new AddressException("Single email address is expected. Actual number of emails is " + emailAddresses.length);
		}
		
		return emailAddresses[0];
	}
	
	public static InternetAddress[] parseEmailAddresses(final String addresses) throws AddressException {
		InternetAddress[] emailAddresses = InternetAddress.parse(addresses);
		
		if (emailAddresses.length < 1) {
			throw new AddressException("At least one address is expected");
		}
		
		return emailAddresses;
	}
	
	/**
	 * Logs the error with ERROR log level and wraps it in
	 * {@link SAPMailingException}
	 * 
	 * @param message
	 *            message for the log event and to be used as message for
	 *            {@link SAPMailingException}
	 * @param cause
	 *            cause exception
	 * @return SAPMailingException with message {@code message} and cause
	 *         {@code cause}
	 */	
	private static SAPMailingException error(final String message, Exception cause) {
		log.error(message, cause);
		return new SAPMailingException(message, cause);
	}

}
