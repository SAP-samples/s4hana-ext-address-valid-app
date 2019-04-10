package com.sap.cloud.s4hana.eventing.businesspartner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.businesspartner.model.CustomBusinessPartner;
import com.sap.cloud.s4hana.eventing.businesspartner.service.CustomBusinessPartnerService;
import com.sap.cloud.s4hana.eventing.core.exceptions.RestExceptionHandler;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPSecurityException;
import com.sap.cloud.s4hana.eventing.security.AddressConfirmationToken;
import com.sap.cloud.s4hana.eventing.security.ExecuteWithJwtTokenFromXsuaa;
import com.sap.cloud.s4hana.eventing.security.HashUtils;
import com.sap.cloud.s4hana.eventing.security.RSACipher;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

/**
 * {@link RestController} which handles communication with the UI.
 * <p> 
 * The error handling is done in {@link RestExceptionHandler}
 */
@RestController
@RequestMapping(BusinessPartnerController.PATH)
public class BusinessPartnerController {
    
    public static final String PATH = "/rest/businesspartner";
    
    private CustomBusinessPartnerService businessPartnerService;
    private RSACipher cipher;
    
    @Autowired
    public BusinessPartnerController(CustomBusinessPartnerService businessPartnerService,
    		RSACipher cipher) {
    	
        this.businessPartnerService = businessPartnerService;
        this.cipher = cipher;
    }
    
    /**
	 * Fetches the Business Partner Address from S/4HANA
	 * <p>
	 * This endpoint is not protected by the Spring Security / OAuth scope and
	 * therefore should only allow requests with a valid token.
	 * 
	 * @param encryptedToken
	 *            {@link AddressConfirmationToken} string encrypted with
	 *            {@link RSACipher}. It authorizes the current user and contains
	 *            the keys of {@link BusinessPartnerAddress} to be fetched.
	 * 
	 * @return {@link BusinessPartnerAddress} as a {@link AddressDTO} Data
	 *         Transfer Object
	 * 
	 * @see AddressConfirmationToken
	 * @see RSACipher#encrypt(java.io.Serializable)
	 * @see RSACipher#decrypt(java.io.Serializable)
	 */
    @GetMapping("/address")
    @ExecuteWithJwtTokenFromXsuaa
    public AddressDTO getBusinessPartnerAddress(@RequestParam(value = "token", defaultValue = "0") final String encryptedToken) {
        AddressConfirmationToken token = cipher.decrypt(encryptedToken);
        if (!token.isValid()){
            throw new SAPSecurityException("Token expired. "
            		+ "Please contact your contact person to resend the address confirmation email.");
        }
        
        return AddressDTO.of(businessPartnerService.getAddressByKeys(token.getBusinessPartner(), token.getAddressID()));
    }
    
    
    /**
     * Updates the Business Partner Address in S/4HANA
     * <p>
	 * This endpoint is not protected by the Spring Security / OAuth scope and
	 * therefore should only allow requests with a valid token.
	 * 
	 * @param address Data Transfer Object that contains {@link BusinessPartnerAddress}
	 * 
	 * @param encryptedToken
	 *            {@link AddressConfirmationToken} string encrypted with
	 *            {@link RSACipher}
	 * 
	 * @see AddressConfirmationToken
	 * @see RSACipher#encrypt(java.io.Serializable)
	 * @see RSACipher#decrypt(java.io.Serializable)
	 */
    @PatchMapping("/address")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExecuteWithJwtTokenFromXsuaa
    public void updateBusinessPartnerAddress(@RequestBody AddressDTO address,
            @RequestParam("token") final String encryptedToken) {
    	
        AddressConfirmationToken token = cipher.decrypt(encryptedToken);
        if (!token.isValid()){
            throw new SAPSecurityException("Token expired. "
            		+ "Please contact your contact person to resend the address confirmation email.");
        }
        
        businessPartnerService.updateAddress(address);
        
        CustomBusinessPartner businessPartner = CustomBusinessPartner.newInstance();
        
        businessPartner.setAddressConfirmationState(AddrConfState.CONFIRMED);
        businessPartner.setAddressChecksum(HashUtils.hash(address.toString()));
        businessPartner.setBusinessPartner(address.getBusinessPartner());
        businessPartnerService.updateBusinessPartner(businessPartner);
    }
    
}
