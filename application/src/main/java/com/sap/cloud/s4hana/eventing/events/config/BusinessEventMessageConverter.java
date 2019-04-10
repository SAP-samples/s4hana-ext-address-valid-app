package com.sap.cloud.s4hana.eventing.events.config;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.s4hana.eventing.events.model.BusinessEvent;

/**
 * This class is a replacement for {@link MappingJackson2MessageConverter} that
 * cannot be used since events from SAP S/4HANA do not contain type id property
 * which MUST be set via
 * {@link MappingJackson2MessageConverter#setTypeIdPropertyName(String)}.
 */
@Component
public class BusinessEventMessageConverter implements MessageConverter {
	
	private static final Logger log = LoggerFactory.getLogger(BusinessEventMessageConverter.class);
	
	private final ObjectMapper om;
	
	@Autowired
	public BusinessEventMessageConverter() {
		om = new ObjectMapper();
		
		// We only define properties that we need in our event model 
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		throw new UnsupportedOperationException("Not implemented");
	}	
	
	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		final byte[] messageBytes = extractMessageBytes(message);
		
		try {
			return om.readValue(messageBytes, BusinessEvent.class);
		} catch (IOException e) {
			final String errorMessage = "Error while trying to convert JMS message body from JSON";
			log.warn(errorMessage, e);
			throw new MessageConversionException(errorMessage, e);
		}
	}
	
	@VisibleForTesting
	protected static byte[] extractMessageBytes(Message m) throws MessageConversionException {
		try {
			return m.getBody(byte[].class);
		} catch (JMSException e) {
			final String message = "Error while trying to extract JMS message body bytes";
			log.warn(message, e);
			throw new MessageConversionException(message, e);
		}
	}

	@VisibleForTesting
	public ObjectMapper getObjectMapper() {
		return om;
	}
	
}
