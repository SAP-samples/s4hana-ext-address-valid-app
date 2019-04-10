package com.sap.cloud.s4hana.eventing.sendmail;

import java.io.IOException;
import java.io.StringWriter;

import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class AddressChangeNotificationService {
	
	private static final Logger log = LoggerFactory.getLogger(AddressChangeNotificationService.class); 
	
	public static final String TEMPLATE_FILE = "notification-address-change.ftlh";
	
	@Autowired
	private Session mailSession;
	
	@Autowired
	private Configuration templateConfiguration;
	
	/**
	 * Sends email to {@link AddressChangeNotification#getEmailAddress()} about a
	 * change in {@link AddressChangeNotification#getAddress()} of
	 * {@link AddressChangeNotification#getBusinessPartner()}. The name and
	 * surname of the contact person are taken from
	 * {@link AddressChangeNotification#getContact()}
	 * 
	 * @param notification
	 *            data model for a Freemarker template
	 * 
	 */
	public void sendMail(AddressChangeNotification notification) throws SAPMailingException {
		final String partnerEmailAddress = notification.getEmailAddress();
    	
    	final String subject = "Address of business partner " 
	    	+ notification.getBusinessPartner().getBusinessPartnerFullName() 
	    	+ " was changed";
    	
    	log.debug("Try to send email notification to {} email address of {} contact "
    			+ "of {} business partner about {} address change",
    			notification.getEmailAddress(),
    			notification.getContact(),
    			notification.getBusinessPartner(),
    			notification.getAddress());
    	
		final String body = processTemplate(notification);
			
		Email.forSession(mailSession)
			.to(partnerEmailAddress)
			.subject(subject)
			.body(body)
			.send();
    }

	/**
	 * Processes FreeMarker template from file {@link AddressChangeNotificationService#TEMPLATE_FILE}
	 * 
	 * @param notification
	 *            data model for template
	 * @return string that contains HTML generated as a result of the template's
	 *         processing
	 * @throws SAPMailingException
	 *             if a template can not be read or there was an error during
	 *             the template's processing
	 * 
	 * @see AddressChangeNotificationService#TEMPLATE_FILE
	 * @see Template#process(Object, java.io.Writer)
	 */
	protected String processTemplate(AddressChangeNotification notification) throws SAPMailingException {
		try {
			final Template template = templateConfiguration.getTemplate(TEMPLATE_FILE);
			
			final StringWriter resultWriter = new StringWriter();
			template.process(notification, resultWriter);
			
			return resultWriter.toString();
		} catch (IOException e) {
			final String message = e.getClass().getSimpleName() + 
					" when trying to get email notification template";
			log.error(message);
			throw new SAPMailingException(message, e);
		} catch (TemplateException e) {
			final String message = e.getClass().getSimpleName() + 
					" when trying to proceess email notification template "
					+ "with business partner " + notification.getBusinessPartner().getBusinessPartner();
			log.error(message);
			throw new SAPMailingException(message, e);
		}
	}

}
