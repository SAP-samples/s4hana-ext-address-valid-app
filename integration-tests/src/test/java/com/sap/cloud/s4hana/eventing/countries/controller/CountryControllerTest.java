package com.sap.cloud.s4hana.eventing.countries.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.s4hana.eventing.countries.controller.CountryController;
import com.sap.cloud.s4hana.eventing.security.AddressConfirmationToken;
import com.sap.cloud.s4hana.eventing.security.RSACipher;
import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class CountryControllerTest {

    @ClassRule
	public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
    
    @Rule
	public WireMockRule wireMockRule = environmentMock.mockS4Hana();
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private RSACipher cipher;
    
    private BusinessPartnerAddress address;
    private String encryptedValidToken;
    private String encryptedExpiredToken;
    
    @Before
    public void setUp() {
        address = EntitySupplier.getExistingAddress();
        
        AddressConfirmationToken validToken = AddressConfirmationToken.of(address, 
        		/* numberOfDaysValid = */ 1);
        encryptedValidToken = cipher.encrypt(validToken);
        
		AddressConfirmationToken expiredToken = AddressConfirmationToken.of(address,
				/* numberOfDaysValid = */ -1);
        encryptedExpiredToken = cipher.encrypt(expiredToken);
    }
    
    @Test
    public void testGetCountries() throws Exception {
        mockMvc.perform(get(CountryController.PATH)
        			.param("token", encryptedValidToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.CountryName == 'Germany')].Country", contains("DE")));
    }
    
    @Test
    public void testGetCountriesWithInvalidToken() throws Exception {
		testGetCountriesWithInvalidToken("Invalid Token");
    }
    
    @Test
    public void testGetCountriesWithExpiredToken() throws Exception {
		testGetCountriesWithInvalidToken(encryptedExpiredToken);
    }

	public void testGetCountriesWithInvalidToken(final String invalidToken) throws Exception {
		mockMvc.perform(get(CountryController.PATH)
        			.param("token", invalidToken))
                .andExpect(status().isUnauthorized());
	}
    
}    
