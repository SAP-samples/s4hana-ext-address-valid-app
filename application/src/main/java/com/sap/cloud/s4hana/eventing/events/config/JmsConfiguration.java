package com.sap.cloud.s4hana.eventing.events.config;

import javax.jms.Session;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import com.sap.cloud.servicesdk.xbem.api.MessagingService;
import com.sap.cloud.servicesdk.xbem.connector.sapcp.MessagingServiceInfoProperties;

@Configuration
@EnableJms
@Profile("cloud")
public class JmsConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(JmsConfiguration.class);
	
	/**
	 * A format string to construct a remote URI for
	 * {@link JmsConnectionFactory#setRemoteURI(String)}
	 * <p>
	 * Use {@link String#format(String, Object...)} to replace the {@code %s}
	 * string parameter with the default remote URI.
	 * <p>
	 * In case your application disconnects from the SAP Business Technology Platform (BTP)
	 * Enterprise Messaging Service with the
	 * {@code o.a.q.j.p.failover.FailoverProvider: Failed to connect after: 20 attempt(s)}
	 * error message, consider increasing {@code failover.maxReconnectAttempts}
	 * or even changing it to the default value {@code -1}.
	 */
	private static final String REMOTE_URI_WITH_FAILOVER = "failover:(%s)?failover.maxReconnectAttempts=20"
			+ "&failover.initialReconnectDelay=30000&failover.useReconnectBackOff=false&failover.reconnectDelay=3000";
	
	private MessagingService messagingService;
	
	@Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(BusinessEventMessageConverter messageConverter) throws Exception {
		log.info("Intitializing the default JmsListenerContainerFactory");
		
		try {
			MessagingService messagingService = grantMessagingService();		
			final JmsConnectionFactory connectionFactory = messagingService.configure(JmsConnectionFactory.class);
			
			connectionFactory.setRemoteURI(String.format(REMOTE_URI_WITH_FAILOVER, connectionFactory.getRemoteURI()));
			
			DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
			factory.setConnectionFactory(connectionFactory);
			factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
			
			// Necessary to get it work with enterprise messaging
			factory.setSessionTransacted(false);
			
			// Enable deserialization of events to Java types
			factory.setMessageConverter(messageConverter);
			
			// Log all errors
			factory.setErrorHandler(e -> log.warn("JMS error: the message is dropped", e));

			return factory;
		} catch (Exception e) {
			log.error("Error while initializing the default JmsListenerContainerFactory. Events from S/4HANA will NOT be processed", e);
			throw e;
		}
    }
	
	private synchronized MessagingService grantMessagingService() {
		if (messagingService == null) {
			final Cloud cloud = new CloudFactory().getCloud();
			ServiceConnectorConfig config = MessagingServiceInfoProperties.init().finish();
			messagingService = cloud.getSingletonServiceConnector(MessagingService.class, config);
		}
		
		return messagingService;
	}
	
}
