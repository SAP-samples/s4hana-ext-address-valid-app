package com.sap.cloud.s4hana.eventing.businesspartner.service;

import static com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState.INITIAL;
import static com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState.OPEN;

import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.businesspartner.model.CustomBusinessPartner;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;
import com.sap.cloud.s4hana.eventing.security.AddressConfirmationToken;
import com.sap.cloud.s4hana.eventing.security.HashUtils;
import com.sap.cloud.s4hana.eventing.security.RSACipher;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotification;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotificationService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BPContactToFuncAndDept;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

@Service
public class AddressConfirmationService {
	
	private static final Logger log = LoggerFactory.getLogger(AddressConfirmationService.class);
	
	/**
	 * Default token validity period in days
	 */
	public static final int DEFAULT_NUMBER_OF_DAYS_VALID = 4;
	
    private final CustomBusinessPartnerService customBusinessPartnerService;
    private final AddressChangeNotificationService notificationService;
    private final RSACipher cipher;
    
    private String addressConfirmationUrl;
    private final int numberOfDaysValid;
    
    @Autowired
    public AddressConfirmationService(CustomBusinessPartnerService customBusinessPartnerService, 
            AddressChangeNotificationService notificationService,
            RSACipher cipher,
            @Value("${addressConfirmation.url}") String confirmationLink,
			@Value("${security.token.numberOfDaysValid:" + DEFAULT_NUMBER_OF_DAYS_VALID + "}") int numberOfDaysValid) {
        
        this.customBusinessPartnerService = customBusinessPartnerService;
        this.notificationService = notificationService;
        this.cipher = cipher;
        this.addressConfirmationUrl = confirmationLink;
        this.numberOfDaysValid = numberOfDaysValid;
    }
    
    public void confirmAddress(@Valid String businessPartnerKey) {
    	log.debug("Confirm the address of a business partner with key {}", businessPartnerKey);
    	
        CustomBusinessPartner businessPartner = customBusinessPartnerService.getBusinessPartnerRootByKey(businessPartnerKey);
        
        log.debug("Confirm the address of a business partner {}", businessPartner);
        
        if (StringUtils.isBlank(businessPartner.getCustomer()) || StringUtils.isNotBlank(businessPartner.getIsNaturalPerson())) {
            log.debug("Business partner {} is neither a Customer nor a Person. Therefore the address will not be confirmed", businessPartner);
            return;
        }
        
        // get address
        BusinessPartnerAddress address = customBusinessPartnerService.getAddress(businessPartner);
        if (address == null) {
            log.debug("No address assigned to business partner {}", businessPartnerKey);
            businessPartner.setAddressChecksum("");
            businessPartner.setAddressConfirmationState(INITIAL);
            customBusinessPartnerService.updateBusinessPartner(businessPartner);
            return;
        }
        
        // check if the address was changed
        final String oldAddressChecksum = businessPartner.getAddressChecksum();
        final String newAddressCheckSum = HashUtils.hash(AddressDTO.of(address).toString());
        final boolean addressChanged = !Objects.equals(newAddressCheckSum, oldAddressChecksum);
        
        final AddrConfState oldAddressConfirmationState = businessPartner.getAddressConfirmationState();
        
		// send emails only if the address was changed or the confirmation state is initial 
        if (!(addressChanged || INITIAL.equals(oldAddressConfirmationState))) {
			log.debug("No email will be sent to business partner {}: "
					+ "the address in {} confirmation state was{} changed",
					businessPartnerKey,
                    oldAddressConfirmationState.toString(),
                    addressChanged ? "" : " NOT");
			
            return;
        }
        
        if (OPEN.equals(oldAddressConfirmationState)) {
			log.debug("No email will be sent to business partner {}: "
					+ "one have already been sent before, will not bother its contact person anymore",
					businessPartnerKey);
        	
            return;
        }
             
        // set confirmation state and address hash
        businessPartner.setAddressConfirmationState(INITIAL);
        businessPartner.setAddressChecksum(newAddressCheckSum);
        
        try {
	        sendAddressConfirmationEmail(businessPartner, address);
	        businessPartner.setAddressConfirmationState(OPEN);
        } catch (SAPMailingException e) {
        	log.debug("Error when sending email", e);
        }
        
        // update the business partner
        if (addressChanged || !businessPartner.getAddressConfirmationState().equals(oldAddressConfirmationState)) {
        	customBusinessPartnerService.updateBusinessPartner(businessPartner);   
        }
    }
    
    /**
     * Sends a confirmation email to the contact person of
     * {@code businessPartner}.
     * 
     * @return {@code true} if an email was send.<br>
     *         {@code false} if the Business Partner has no contact person or no
     *         valid email address can be found.
     */
	@VisibleForTesting
    protected void sendAddressConfirmationEmail(CustomBusinessPartner businessPartner, BusinessPartnerAddress address) {
        // get contact
        BPContactToFuncAndDept contact = customBusinessPartnerService.determineResponsibleContact(businessPartner);
        
        if (StringUtils.isBlank(contact.getEmailAddress())) {
            throw new SAPMailingException(String.format(
                    "No valid email address found for business partner %s. Therefore no email was send",
                    businessPartner.getBusinessPartner()));
        }
            
        // get contact person
        BusinessPartner contactPerson = customBusinessPartnerService.getBusinessPartnerByKey(contact.getBusinessPartnerPerson());
        
        // construct link
        final AddressConfirmationToken urlToken = AddressConfirmationToken.of(address, numberOfDaysValid);
        final String encryptedToken = new String(cipher.encrypt(urlToken));
        final String confirmationUrl = String.format(addressConfirmationUrl, encryptedToken);
        
        // construct notification
        final AddressChangeNotification notification = AddressChangeNotification.of(
                businessPartner, 
                AddressDTO.of(address),
                contactPerson, 
                contact.getEmailAddress(), 
                confirmationUrl);
        
        // send email
        notificationService.sendMail(notification);
        
        log.debug("Email has been sent to {}", contact.getEmailAddress());
    }

}
