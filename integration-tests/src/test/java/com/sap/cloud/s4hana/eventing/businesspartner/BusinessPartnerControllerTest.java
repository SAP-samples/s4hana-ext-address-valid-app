package com.sap.cloud.s4hana.eventing.businesspartner;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class BusinessPartnerControllerTest {

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
    public void testGetBusinessPartnerAddress() throws Exception {
        mockMvc.perform(get(ADDRESS_ENDPOINT)
                	.param("token", encryptedValidToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessPartner", is(address.getBusinessPartner())))
                .andExpect(jsonPath("$.addressID", is(address.getAddressID())));
    }
    
    @Test
    @Ignore // this can only be tested against a real S/4HANA system, not against WireMock
    public void testUpdateBupdaAddress() throws Exception {        
        MockHttpServletResponse response = mockMvc.perform(get(ADDRESS_ENDPOINT)
                	.param("token", encryptedValidToken))
                .andReturn()
                .getResponse();
        
        BusinessPartnerAddress address = toAddress(response.getContentAsString());
        String originalStreet = address.getStreetName();
        
        address.setStreetName("Some New Street");
        mockMvc.perform(patch(ADDRESS_ENDPOINT)
	                .param("token", encryptedValidToken)
	                .content(toJson(address))
	                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
        
        address.setStreetName(originalStreet);
        mockMvc.perform(patch(ADDRESS_ENDPOINT)
	                .param("token", encryptedValidToken)
	                .content(toJson(address))
	                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
    }
    
    @Test
    public void testGetBusinessPartnerAddressWithInvalidToken() throws Exception {
		testGetBusinessPartnerAddressWithInvalidToken("Invalid Token");
    }
    
    @Test
    public void testGetBusinessPartnerAddressWithExpiredToken() throws Exception {
		testGetBusinessPartnerAddressWithInvalidToken(encryptedExpiredToken);
    }

	public void testGetBusinessPartnerAddressWithInvalidToken(final String invalidToken) throws Exception {
		mockMvc.perform(get(ADDRESS_ENDPOINT)
        			.param("token", invalidToken))
                .andExpect(status().isUnauthorized());
	}
    
    @Test
    public void testPatchBusinessPartnerAddressWithInvalidToken() throws Exception {
		testPatchBusinessPartnerAddressWithInvalidToken("Invalid Token");
    }
    
    @Test
    public void testPatchBusinessPartnerAddressWithExpiredToken() throws Exception {
		testPatchBusinessPartnerAddressWithInvalidToken(encryptedExpiredToken);
    }

	public void testPatchBusinessPartnerAddressWithInvalidToken(final String invalidToken)
			throws Exception, JsonProcessingException {
		mockMvc.perform(patch(ADDRESS_ENDPOINT)
	                .param("token", invalidToken)
	                .content(toJson(EntitySupplier.getDefaultAddress()))
	                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
	}
    
    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
    
    private BusinessPartnerAddress toAddress(String json) throws JsonParseException, JsonMappingException, IOException {
        return new ObjectMapper().readValue(json, BusinessPartnerAddress.class);
    }
}
