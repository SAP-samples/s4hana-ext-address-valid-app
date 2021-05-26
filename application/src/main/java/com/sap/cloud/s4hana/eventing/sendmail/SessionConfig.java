package com.sap.cloud.s4hana.eventing.sendmail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPException;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;
import com.sap.cloud.s4hana.eventing.security.NoRequestContextDestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.GenericDestination;

/**
 * Creates JavaMail {@link Session} using configuration stored in MAIL
 * destination on CloudFoundry
 */
@Configuration
public class SessionConfig {

	private static final Logger log = LoggerFactory.getLogger(SessionConfig.class);
	
	/**
	 * The defulat name of default SAP Business Technology Platform (BTP) destination of type
	 * {@code MAIL}.
	 */
	public static final String DEFAULT_MAIL_DESTINATION_NAME = "MailSession";
	public static final String PROPERTY_MAIL_PASSWORD = "mail.password";
	public static final String PROPERTY_MAIL_USER = "mail.user";
	
	private final NoRequestContextDestinationAccessor noRequestContextDestinationAccessor;
	private final String destinationName;

	/**
	 * @param destinationName
	 *            The name of SAP Business Technology Platform (BTP) mail destination. If the
	 *            provided value is blank (null or empty even when trimmed),
	 *            then {@link SessionConfig#DEFAULT_MAIL_DESTINATION_NAME} is
	 *            used as a default value.
	 */
	@Autowired
	public SessionConfig(NoRequestContextDestinationAccessor noRequestContextDestinationAccessor,
			@Value("${addressConfirmation.destination:}") String destinationName) throws SAPException {
		
		if (StringUtils.isBlank(destinationName)) {
			destinationName = DEFAULT_MAIL_DESTINATION_NAME;
		}
		this.destinationName = destinationName;
		
		this.noRequestContextDestinationAccessor = noRequestContextDestinationAccessor;
	}

	/**
	 * @return {@link Session} constructed from destination with default name
	 *         {@link MAIL_DESTINATION_NAME}
	 * @throws SAPMailingException
	 *             when destination is not found or cannot be accessed
	 * @see Session
	 * @see SessionConfig#getMailDestination(String)
	 * @see SessionConfig#DEFAULT_MAIL_DESTINATION_NAME
	 */
	@Bean
	public Session getDefaultSession() throws SAPMailingException {
		return getSession(destinationName);
	}

	/**
	 * @param destinationName
	 *            the name of a mail destination on SAP Business Technology Platform (BTP)
	 * @return {@link Session} constructed from destination with specified
	 *         {@code name}
	 * @throws SAPMailingException
	 *             when destination is not found or cannot be accessed
	 * @see Session
	 * @see SessionConfig#getMailDestination(String)
	 */    
	public Session getSession(final String destinationName) throws SAPMailingException {
		
		// replace with getGenericDestination() when CF doesn't work
		// final GenericDestination mailDestination = getGenericDestination(DESTINATION_NAME);
		final GenericDestination mailDestination = getMailDestination(destinationName);
		
		Properties properties = new Properties(); 
		properties.putAll(mailDestination.getPropertiesByName());
		
		final String username = properties.getProperty(PROPERTY_MAIL_USER);
		final String password = properties.getProperty(PROPERTY_MAIL_PASSWORD);
		
		final Authenticator authenticator = new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
			
		};
		
		return Session.getInstance(properties, authenticator);
	}

	/**
	 * @param name
	 *            the name of a mail destination on SAP Business Technology Platform (BTP)
	 * @return SAP Business Technology Platform (BTP) mail destination by its name
	 * @throws SAPMailingException
	 *             when destination is not found, cannot be accessed or not of
	 *             type {@link DestinationType.MAIL}
	 * @see SessionConfig#DEFAULT_MAIL_DESTINATION_NAME
	 */
	public GenericDestination getMailDestination(final String name) throws SAPMailingException {
		// get serialized keys from the corresponding destination properties
		final GenericDestination mailDestination = noRequestContextDestinationAccessor.getGenericDestination(name);
    	
    	if (!DestinationType.MAIL.equals(mailDestination.getDestinationType())) {
			final String message = "Destination " + name + " is not of type MAIL.";
			log.error(message);
			throw new SAPMailingException(message);
		}
    	
		return mailDestination;
    }

}