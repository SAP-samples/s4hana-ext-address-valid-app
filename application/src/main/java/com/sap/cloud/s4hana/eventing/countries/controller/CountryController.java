package com.sap.cloud.s4hana.eventing.countries.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPSecurityException;
import com.sap.cloud.s4hana.eventing.countries.service.CountryService;
import com.sap.cloud.s4hana.eventing.security.AddressConfirmationToken;
import com.sap.cloud.s4hana.eventing.security.ExecuteWithJwtTokenFromXsuaa;
import com.sap.cloud.s4hana.eventing.security.RSACipher;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.country.CountryText;

@RestController
@RequestMapping(CountryController.PATH)
public class CountryController {
    
    public static final String PATH = "/rest/countries";
    
	private CountryService countryService;
	private RSACipher cipher;
    
    @Autowired
    public CountryController(CountryService countryService, RSACipher cipher) {
        this.cipher = cipher;
        this.countryService = countryService;
    }
    
    /**
	 * Fetches the list of countries from S/4HANA
	 * <p>
	 * This endpoint is not protected by the Spring Security / OAuth scope and
	 * therefore should only allow requests with a valid token.
	 * 
	 * @param encryptedToken
	 *            {@link AddressConfirmationToken} string encrypted with
	 *            {@link RSACipher}. It authorizes the current user mainly to
	 *            prevent DOS attacks on the open endpoint.
	 * 
	 * @return the list of countries from S/4HANA
	 * 
	 * @see AddressConfirmationToken
	 * @see RSACipher#encrypt(java.io.Serializable)
	 * @see RSACipher#decrypt(java.io.Serializable)
	 */
    @GetMapping
    @ExecuteWithJwtTokenFromXsuaa
    public List<CountryText> getAll(@RequestParam("token") final String encryptedToken) {
    	
        AddressConfirmationToken token = cipher.decrypt(encryptedToken);
        if (!token.isValid()){
            throw new SAPSecurityException("Token expired. "
            		+ "Please contact your contact person to resend the address confirmation email.");
        }
        
        return countryService.getAll();
    } 
    
}
