package com.sap.cloud.s4hana.eventing.events.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.sap.cloud.s4hana.eventing.businesspartner.service.AddressConfirmationService;
import com.sap.cloud.s4hana.eventing.events.model.BusinessPartnerEvent;

public class BusinessPartnerMessageConsumerTest {
	
	@Rule 
	public MockitoRule rule = MockitoJUnit.rule();
	
	@Mock
	AddressConfirmationService addressConfirmationServiceMock;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	BusinessPartnerEvent eventMock;
	
	@InjectMocks
	BusinessPartnerMessageConsumer testee;
	
	@Test
	public void testOnEventThenConfirmationServiceIsCalledWithTheBusinessPartnerKey() {
		// Given an event mock that returns the expected business partner key
		final String expectedBusinessPartnerKey = "ExpectedBusinessPartnerKey";
		when(eventMock.getPayload().getBusinessPartnerKey()).thenReturn(expectedBusinessPartnerKey);
		
		// When the event handler is called
		testee.onEvent(eventMock);
		
		// Then the AddressConfirmationService is called with the expected business partner key
		verify(addressConfirmationServiceMock).confirmAddress(expectedBusinessPartnerKey);
	}

}
