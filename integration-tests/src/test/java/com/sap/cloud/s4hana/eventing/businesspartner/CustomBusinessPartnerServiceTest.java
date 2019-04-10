package com.sap.cloud.s4hana.eventing.businesspartner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.businesspartner.model.CustomBusinessPartner;
import com.sap.cloud.s4hana.eventing.businesspartner.service.CustomBusinessPartnerService;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPODataException;
import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BPContactToFuncAndDept;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CustomBusinessPartnerServiceTest {
	
	@Rule
	public WireMockRule wireMockRule = environmentMock.mockS4Hana();
    
    @ClassRule
    public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
    
    @Autowired
    public CustomBusinessPartnerService businessPartnerService;
    
    @Test
    public void testGetRootByKey() throws ODataException {
        BusinessPartner businessPartner = businessPartnerService.getBusinessPartnerRootByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        
        assertThat("BusinessPartnerKey", businessPartner.getBusinessPartner(), is(equalTo(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY)));
    }
    
    @Test 
    public void testGetByKey() {
        BusinessPartner businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        
        assertThat("BusinessPartnerKey", businessPartner.getBusinessPartner(), is(equalTo(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY)));
    }
     
    @Test
    @Ignore // this can only be tested against a real S/4HANA system, not against WireMock
    public void testUpdateBusinessPartner() {
    	// given a business partner
    	BusinessPartner businessPartner = businessPartnerService.getBusinessPartnerRootByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
    	final String oldName = businessPartner.getOrganizationBPName2();
    	
    	// when its name is updated
    	final String newName = "New name";
		businessPartner.setOrganizationBPName2(newName);
		businessPartnerService.updateBusinessPartner(businessPartner);
		
		// then the new name stored in SAP S/4HANA Cloud 
    	BusinessPartner updatedBusinessPartner = businessPartnerService.getBusinessPartnerRootByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        assertThat("BusinessPartner's FullName", updatedBusinessPartner.getOrganizationBPName2(), is(newName));
        
        // after the test: revert the name back to the original one
        businessPartner.setBusinessPartnerFullName(oldName);
		businessPartnerService.updateBusinessPartner(businessPartner);
    }
    
    @Test(expected = SAPODataException.class)
    public void testGetByNoneExistingKey () {
        businessPartnerService.getBusinessPartnerByKey(EntitySupplier.NONEXISTENT_BUSINESS_PARTNER_KEY);
    }
    
    @Test
    @Ignore // this can only be tested against a real S/4HANA system, not against WireMock
    public void testSetAddrConfStateOnBusinessPartner() throws ODataException {
        CustomBusinessPartner businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        AddrConfState originalState = businessPartner.getAddressConfirmationState();
        
        setAddrConfStateOnBusinessPartner(businessPartner.getBusinessPartner(), AddrConfState.CONFIRMED);
        businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        assertThat("Address Confirmation State on BUPA", 
                businessPartner.getAddressConfirmationState(), is(AddrConfState.CONFIRMED));
        
        setAddrConfStateOnBusinessPartner(businessPartner.getBusinessPartner(), originalState);
        businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        assertThat(originalState, is(businessPartner.getAddressConfirmationState()));
    }
    
    @Test
    @Ignore // this can only be tested against a real S/4HANA system, not against WireMock
    public void testSetAddressChecksumOnBusinessPartner() throws ODataException {
        CustomBusinessPartner businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        String originalChecksum = businessPartner.getAddressChecksum();
        
        final String dummyChecksum = UUID.randomUUID().toString();
		setAddressChecksumOnBusinessPartner(businessPartner.getBusinessPartner(), dummyChecksum);
        businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        assertThat("Address Checksum on BUPA", 
                businessPartner.getAddressChecksum(), is(dummyChecksum));
        
        setAddressChecksumOnBusinessPartner(businessPartner.getBusinessPartner(), originalChecksum);
        businessPartner = businessPartnerService.getBusinessPartnerByKey(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);
        assertThat(originalChecksum, is(businessPartner.getAddressChecksum()));
    }
    
    @Test
    public void testGetBPContactToFuncAndDeptByBPCompanyKey() {
        List<BPContactToFuncAndDept> contacts = businessPartnerService.getContacts(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY);

        assertThat("all contacts should have business partner company equals to the expected one", 
        		contacts, everyItem(hasProperty("businessPartnerCompany", equalTo(EntitySupplier.EXISTING_BUSINESS_PARTNER_KEY))));
    }
    
    @Test
    public void testGetBPContactToFuncAndDeptByNoneExistingBPCompanyKey() {
        List<BPContactToFuncAndDept> contacts = businessPartnerService.getContacts(EntitySupplier.NONEXISTENT_BUSINESS_PARTNER_KEY);
        assertThat(contacts, is(empty()));
    }
    
    @Test
    public void testGetAddressByKeys() {
    	// Given an address that exists in S/4HANA ...
        BusinessPartnerAddress existingAddress = EntitySupplier.getExistingAddress();
        
        // When
        existingAddress = businessPartnerService.getAddressByKeys(existingAddress.getBusinessPartner(), existingAddress.getAddressID());
        
        // Then
        assertThat("the address should have expected key and business partner company", 
        		existingAddress, allOf(hasProperty("businessPartner", equalTo(existingAddress.getBusinessPartner())), 
        				hasProperty("addressID", equalTo(existingAddress.getAddressID()))));
    }
    
    @Test
    @Ignore // this can only be tested against a real S/4HANA system, not against WireMock
    public void testUpdateBusinessPartnerAddress() {
    	// Given an address that exists in S/4HANA ...
        BusinessPartnerAddress existingAddress = EntitySupplier.getExistingAddress();
        existingAddress = businessPartnerService.getAddressByKeys(existingAddress.getBusinessPartner(), existingAddress.getAddressID());

        // ... and a new city name
        final String newCityName = "City" + UUID.randomUUID(); 
        
        // ... and a corresponding Address DTO that contains new city name 
        AddressDTO changedAddressDTO = AddressDTO.of(existingAddress);
        changedAddressDTO.setCityName(newCityName);
        
        // When the address is updated using the updated DTO
        businessPartnerService.updateAddress(changedAddressDTO);
        
        // Then the updated address contains the new city name ...
        final BusinessPartnerAddress updatedAddress = businessPartnerService.getAddressByKeys(changedAddressDTO.getBusinessPartner(), changedAddressDTO.getAddressID());
        assertThat("The cityname of the updated address", updatedAddress.getCityName(), is(newCityName));
        
        // ... and all significant fields are the same
        assertThat("The DTO of the updated address should be the same as the original DTO", 
        		AddressDTO.of(updatedAddress), is(equalTo(changedAddressDTO)));

        // (Optional) Revert city name to the old one
        //businessPartnerService.updateBusinessPartnerAddress(existingAddress);
    }

	private void setAddrConfStateOnBusinessPartner(final String businessPartnerKey, final AddrConfState state) {
	    final CustomBusinessPartner bp = businessPartnerService.getBusinessPartnerRootByKey(businessPartnerKey);
	    bp.setAddressConfirmationState(state);
	    businessPartnerService.updateBusinessPartner(bp);
	}

	private void setAddressChecksumOnBusinessPartner(final String businessPartnerKey, final String checksum) {
	    final CustomBusinessPartner bp = businessPartnerService.getBusinessPartnerRootByKey(businessPartnerKey);
	    bp.setAddressChecksum(checksum);
	    businessPartnerService.updateBusinessPartner(bp);
	}
    
}