package com.sap.cloud.s4hana.eventing.businesspartner.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPODataException;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BPContactToFuncAndDept;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.selectable.BusinessPartnerSelectable;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class CustomBusinessPartnerServiceTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BusinessPartnerService serviceMock;
    
    @Mock
    private ErpConfigContext erpConfigContextMock;
    
    private CustomBusinessPartnerService testee;
    
    @Before
    public void setUp() {
    	testee = new CustomBusinessPartnerService(serviceMock, erpConfigContextMock, null, null);
    }
    
    @Test
    public void testGetByKey() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities ...
        when(serviceMock
                .getBusinessPartnerByKey(businessPartner.getBusinessPartner())
                .select(any(BusinessPartnerSelectable.class))
                .select(any(BusinessPartnerSelectable.class))
                .select(any(BusinessPartnerSelectable.class))
                .execute(any(ErpConfigContext.class)))
        .thenReturn(businessPartner);
        
        // When CustomBusinessPartnerService is called
        BusinessPartner newBusinessPartner = testee.getBusinessPartnerByKey(businessPartner.getBusinessPartner());
                
        // Then the returned BusinessPartner should be matching ...
        assertThat("The right businessPartner should be returned by Service", newBusinessPartner, is(equalTo(businessPartner)));
    }
    
    @Test(expected = SAPODataException.class)
    public void testGetByKeyWithException() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities ...
        when(serviceMock
                .getBusinessPartnerByKey(businessPartner.getBusinessPartner())
                .select(any(BusinessPartnerSelectable.class))
                .select(any(BusinessPartnerSelectable.class))
                .select(any(BusinessPartnerSelectable.class))
                .execute(any(ErpConfigContext.class)))
        .thenThrow(ODataException.class);
        
        // When CustomBusinessPartnerService is called
        BusinessPartner newBusinessPartner = testee.getBusinessPartnerByKey(businessPartner.getBusinessPartner());
                
        // Then there should be a SAPODataException ...
       assertThat("BusinessPartner should be null", newBusinessPartner, is(nullValue()));
    }
    
    @Test(expected = SAPODataException.class)
    public void testGetRootByKeyWithException() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities ...
        when(serviceMock
                .getBusinessPartnerByKey(businessPartner.getBusinessPartner())
                .execute(any(ErpConfigContext.class)))
        .thenThrow(ODataException.class);
        
        // When CustomBusinessPartnerService is called
        BusinessPartner newBusinessPartner = testee.getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
                
        // Then the returned BusinessPartner should be matching ...
        assertThat("The right businessPartner should be returned by Service", newBusinessPartner, is(equalTo(businessPartner)));
    }
    
    @Test
    public void testGetRootByKey() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities ...
        when(serviceMock
                .getBusinessPartnerByKey(businessPartner.getBusinessPartner())
                .execute(any(ErpConfigContext.class)))
        .thenReturn(businessPartner);
        
        // When CustomBusinessPartnerService is called
        BusinessPartner newBusinessPartner = testee.getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
                
        // Then the returned BusinessPartner should be matching ...
        assertThat("The right Business Partner should be returned by Service", newBusinessPartner, is(equalTo(businessPartner)));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testDetermineResponsibleContactWithNoContacts() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities
        when(serviceMock
                .getAllBPContactToFuncAndDept()
                .filter(any(ExpressionFluentHelper.class))
                .execute(any(ErpConfigContext.class)))
        .thenReturn(Collections.emptyList());
        
        // When CustomBusinessPartnerService is called ...
        BPContactToFuncAndDept contactPerson = testee.determineResponsibleContact(businessPartner);
        
        // Then the returned BPContactToFuncAndDept should be matching ...
        assertThat("Contact Person should be a BPContactToFuncAndDept", contactPerson, is(equalTo(new BPContactToFuncAndDept())));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testDetermineResponsibleContactWithDefaultContacts() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities
        when(serviceMock
                .getAllBPContactToFuncAndDept()
                .filter(any(ExpressionFluentHelper.class))
                .execute(any(ErpConfigContext.class)))
        .thenReturn(EntitySupplier.getListOfContacts());
        
        // When CustomBusinessPartnerService is called ...
        BPContactToFuncAndDept contactPerson = testee.determineResponsibleContact(businessPartner);
        
        // Then the returned BPContactToFuncAndDept should be matching ...
        assertThat("Contact Person should be the one with the email", contactPerson, is(equalTo(EntitySupplier.getDefaultContact())));
        assertThat("Contact Person should not be the one without email", contactPerson, is(not(equalTo(EntitySupplier.getContactWithNoEmail()))));
    }
    
	@Test
	@SuppressWarnings("unchecked")
    public void testDetermineResponsibleContactWithAllKindsOfContacts() throws ODataException {
        // Given dummy entities ...
        BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
        
        // ... and a business partner service's mock that returns dummy entities
        when(serviceMock
                .getAllBPContactToFuncAndDept()
                .filter(any(ExpressionFluentHelper.class))
                .execute(any(ErpConfigContext.class)))
        .thenReturn(EntitySupplier.getListOfContactsWithAll());
        
        // When CustomBusinessPartnerService is called ...
        BPContactToFuncAndDept contactPerson = testee.determineResponsibleContact(businessPartner);
        
        // Then the returned BPContactToFuncAndDept should be matching ...
        assertThat("Contact Person should be the one with the email", contactPerson, is(equalTo(EntitySupplier.getQualityAssuranceQualityOfficeContact())));
        assertThat("Contact Person should not be the one without email", contactPerson, is(not(equalTo(EntitySupplier.getQualityAssuranceContact()))));
    }
    
    @Test
    public void TestgetBupaAddress() throws ODataException {
    	// Given a business partner mock...
    	BusinessPartner businessPartnerMock = mock(BusinessPartner.class);
    	
		// ... that returns a dummy address
		when(businessPartnerMock.fetchBusinessPartnerAddress()).thenReturn(EntitySupplier.getDefaultListAddress());
        
        // When CustomBusinessPartnerService is called ...
        BusinessPartnerAddress address = testee.getAddress(businessPartnerMock);
        
        // Then the returned BPContactToFuncAndDept should be matching ...
        assertThat("Address should be the default address", address, is(equalTo(EntitySupplier.getDefaultAddress())));
    }
}
