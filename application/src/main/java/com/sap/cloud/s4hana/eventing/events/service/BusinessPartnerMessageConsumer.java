package com.sap.cloud.s4hana.eventing.events.service;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.eventing.businesspartner.service.AddressConfirmationService;
import com.sap.cloud.s4hana.eventing.events.model.BusinessPartnerEvent;
import com.sap.cloud.s4hana.eventing.security.ExecuteWithJwtTokenFromXsuaa;

@Service
public class BusinessPartnerMessageConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(BusinessPartnerMessageConsumer.class);
    
    private final AddressConfirmationService addressConfirmationService;
    
    @Autowired
    public BusinessPartnerMessageConsumer(AddressConfirmationService addressConfirmationService) {
		this.addressConfirmationService = addressConfirmationService;
	}

	@JmsListener(destination = "${eventing.queue}")
	@ExecuteWithJwtTokenFromXsuaa(error = "There was an exception while handling an event. The event was dropped.")
	public void onEvent(@Valid BusinessPartnerEvent event) {
        log.debug("Handle BusinessPartnerEvent: {}", event);
        
        final String businessPartnerKey = event.getPayload().getBusinessPartnerKey();
        
        addressConfirmationService.confirmAddress(businessPartnerKey);
    }

}
