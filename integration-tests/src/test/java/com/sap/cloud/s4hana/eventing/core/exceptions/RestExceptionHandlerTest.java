package com.sap.cloud.s4hana.eventing.core.exceptions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.s4hana.eventing.businesspartner.controller.BusinessPartnerController;
import com.sap.cloud.s4hana.eventing.security.AddressConfirmationToken;
import com.sap.cloud.s4hana.eventing.security.RSACipher;
import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class RestExceptionHandlerTest {

	    private static final String ADDRESS_ENDPOINT = BusinessPartnerController.PATH + "/address";

	    @ClassRule
		public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
	    
	    @Rule
		public WireMockRule wireMockRule = environmentMock.mockS4Hana();
	    
	    @Autowired
	    private MockMvc mockMvc;
	    
	    @Autowired
	    RSACipher cipher;
	    
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
	    public void testWithExpiredToken() throws Exception {
	        mockMvc.perform(get(ADDRESS_ENDPOINT)
	                	.param("token", encryptedExpiredToken))
	                .andExpect(status().is(equalTo(HttpStatus.UNAUTHORIZED.value())))
	                .andExpect(jsonPath("$.code", is(SAPSecurityException.class.getSimpleName())))
	                .andExpect(jsonPath("$.message", containsString("Token expired")));
	    }
	    
	    @Test
	    public void testWhenConnectionError() throws Exception {
	    	wireMockRule.givenThat(WireMock.get(WireMock.anyUrl()).
					willReturn(WireMock.aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));	
	    	
	        mockMvc.perform(get(ADDRESS_ENDPOINT)
	                	.param("token", encryptedValidToken))
	                .andExpect(status().is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())))
	                .andExpect(jsonPath("$.code", is(SAPODataException.class.getSimpleName())))
	                .andExpect(jsonPath("$.message", containsString("There was an error while retrieving the address")))
	                .andExpect(jsonPath("$.innererror.message", not(isEmptyOrNullString()))); // there is an innen error
	    }

}
