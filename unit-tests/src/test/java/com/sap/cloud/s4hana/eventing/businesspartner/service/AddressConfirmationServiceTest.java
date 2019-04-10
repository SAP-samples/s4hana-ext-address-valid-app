package com.sap.cloud.s4hana.eventing.businesspartner.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;

import static com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState.OPEN;
import static com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState.CONFIRMED;
import static com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState.INITIAL;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.businesspartner.model.CustomBusinessPartner;
import com.sap.cloud.s4hana.eventing.businesspartner.service.CustomBusinessPartnerService;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;
import com.sap.cloud.s4hana.eventing.security.AddressConfirmationToken;
import com.sap.cloud.s4hana.eventing.security.HashUtils;
import com.sap.cloud.s4hana.eventing.security.RSACipher;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotification;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotificationService;
import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BPContactToFuncAndDept;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

/**
 * Different Cases that will be tested here:
 * <ul>
 * <li>Address was changed, State doesn’t matter and Email is sent</li>
 * <li>Address was changed and no Email address</li>
 * <li>Address was not changed, but state is INITIAL</li>
 * <li>Address was not changed, but state is INITIAL and still no Email</li>
 * <li>Address was not changed and state is Confirmed or Open</li>
 * <li>Business Partner is not a customer</li>
 * <li>Business Partner is a person</li>
 * <li>Business Partner has no address</li>
 * <ul>
 */
public class AddressConfirmationServiceTest {
	
	@Rule 
	public MockitoRule rule = MockitoJUnit.rule();
    
    @ClassRule
    public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
    
    @Mock
    private RSACipher cipherMock;
    
    @Mock
    private CustomBusinessPartnerService businessPartnerServiceMock;
    
    @Mock
    private AddressChangeNotificationService notificationServiceMock;
    
    @Captor
    private ArgumentCaptor<AddressChangeNotification> notificationCaptor;
    
    @Mock
    private Environment springEnvironmentMock;
    
    private AddressConfirmationService testee;
    
    @Before
	public void setUp() {
    	testee = new AddressConfirmationService(
        		businessPartnerServiceMock, 
        		notificationServiceMock,
        		cipherMock,
                "confirmation.link/%s",
    			/* numberOfDaysValid = */ 1);
	}

    /**
     * Address was changed, State doesn’t matter and Email is sent
     */
    @Test
    public void testAddressChangeWithContactAndEmail() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BPContactToFuncAndDept contact = EntitySupplier.getDefaultContact();
        final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
        
        // ... and a business partner service's mock that returns dummy entities ...
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerByKey(contact.getBusinessPartnerPerson());
        doReturn(contact).when(businessPartnerServiceMock).determineResponsibleContact(businessPartner);
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);
        
        // ... and cipher's mock that always return the same string ...
        final String urlToken = "RSA-2048 encrypted, Base64 encoded and URL encoded string";
        doReturn(urlToken).when(cipherMock).encrypt(any(AddressConfirmationToken.class));
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is sent ...
        verify(notificationServiceMock).sendMail(notificationCaptor.capture());
        
        final AddressChangeNotification notification = notificationCaptor.getValue();
        assertThat("notification's business partner", 
                notification.getBusinessPartner(), 
                is(businessPartner));
        assertThat("notification's email address", 
                notification.getBusinessPartner(), 
                is(businessPartner));
        assertThat("notification's address", 
                notification.getAddress(), 
                is(AddressDTO.of(address)));
        assertThat("notification's link", 
                notification.getConfirmationLink(), 
                containsString(urlToken));
        
        assertThat("Address Confirmation State after processing",
                businessPartner.getAddressConfirmationState(), 
                is(equalTo(OPEN)));
    }
    
    /**
     * Address was changed, State doesn’t matter and there was an error while sending Email 
     */
    @Test
    public void testAddressChangeWithContactAndEmailWhenErrorSendingEmail() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BPContactToFuncAndDept contact = EntitySupplier.getDefaultContact();
        final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
        
        // ... and a business partner service's mock that returns dummy entities ...
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerByKey(contact.getBusinessPartnerPerson());
        doReturn(contact).when(businessPartnerServiceMock).determineResponsibleContact(businessPartner);
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);
        
        // ... and cipher's mock that always return the same string ...
        final String urlToken = "RSA-2048 encrypted, Base64 encoded and URL encoded string";
        doReturn(urlToken).when(cipherMock).encrypt(any(AddressConfirmationToken.class));
        
        // ... and email notification service that always throw an exception 
        doThrow(new SAPMailingException("mock exception when sending a mail")).when(notificationServiceMock).sendMail(any());
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is sent ...
        verify(notificationServiceMock).sendMail(notificationCaptor.capture());
        
        assertThat("Address Confirmation State after processing should be INITIAL in case the email was not sent",
                businessPartner.getAddressConfirmationState(), 
                is(equalTo(INITIAL)));
    }
    
    /**
     * Address was changed and no Email address 
     */
    @Test
    public void testNoContactsBupa() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BPContactToFuncAndDept contact = new BPContactToFuncAndDept(); // contains no email address
        final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
        
        // ... and a business partner service's mock that returns dummy entities ...        
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(contact).when(businessPartnerServiceMock).determineResponsibleContact(businessPartner);
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is never called ...
        verify(notificationServiceMock, never()).sendMail(null);
        
        assertThat("Address Confirmation State",
                businessPartner.getAddressConfirmationState(), 
                is(equalTo(INITIAL)));
        
        assertThat("Address Checksum after processing", 
                businessPartner.getAddressChecksum(),
                is(HashUtils.hash(AddressDTO.of(address).toString())));
    }
    
    /**
     * Address was not changed, but state is INITIAL 
     */
    @Test
    public void testNoAddressChangeWithContactAndEmailWithInitialAddress() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BPContactToFuncAndDept contact = EntitySupplier.getDefaultContact();
        final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
        
        // ... and a address Checksum on the Businesspartner, that is the same as the current address ...
        businessPartner.setAddressChecksum(HashUtils.hash(AddressDTO.of(address).toString()));
        
        // ... and Address Confirmation State is Initial
        businessPartner.setAddressConfirmationState(INITIAL);
        
        // ... and a business partner service's mock that returns dummy entities ...
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerByKey(contact.getBusinessPartnerPerson());
        doReturn(contact).when(businessPartnerServiceMock).determineResponsibleContact(businessPartner);
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);
        
        // ... and cipher's mock that always return the same string ...
        final String urlToken = "RSA-2048 encrypted, Base64 encoded and URL encoded string";
        doReturn(urlToken).when(cipherMock).encrypt(any(AddressConfirmationToken.class));
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is sent ...
        verify(notificationServiceMock).sendMail(notificationCaptor.capture());
        
        final AddressChangeNotification notification = notificationCaptor.getValue();
        assertThat("notification's business partner", 
                notification.getBusinessPartner(), 
                is(businessPartner));
        assertThat("notification's email address", 
                notification.getBusinessPartner(), 
                is(businessPartner));
        assertThat("notification's address", 
                notification.getAddress(), 
                is(AddressDTO.of(address)));
        assertThat("notification's link", 
                notification.getConfirmationLink(), 
                containsString(urlToken));
        
        // ... and Address Confirmation State is Open
        assertThat("Address Confirmation State after processing",
                businessPartner.getAddressConfirmationState(), 
                is(equalTo(OPEN)));
    }
    
    /**
     * Address was not changed, but state is INITIAL and still no Email
     */
    @Test
    public void testNoAddressChangeWithNoEmailAndInitialAddress() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BPContactToFuncAndDept contact = EntitySupplier.getContactWithNoEmail();
        final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
        
        // ... and a business partner service's mock that returns dummy entities ...
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);
        doReturn(contact).when(businessPartnerServiceMock).determineResponsibleContact(businessPartner);
        
        //... and a address Checksum on the Businesspartner, that is the same as the current address
        businessPartner.setAddressChecksum(HashUtils.hash(AddressDTO.of(address).toString()));
        
        // ... and Address Confirmation State is Initial
        businessPartner.setAddressConfirmationState(INITIAL);
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is never called ...
        verify(notificationServiceMock, never()).sendMail(null);

        //... and Address Confirmation State is still Initial
        assertThat("Address Confirmation State after processing",
                businessPartner.getAddressConfirmationState(), 
                is(equalTo(INITIAL)));
        
    }
    
    /**
     * Address was not changed and state is Confirmed or Open 
     */
    @Test
    public void testNoAddressChangeWithContactAndEmailWithConfirmedAddress() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
        
        // ... and a business partner service's mock that returns dummy entities ...
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);

        //... and a address Checksum on the Businesspartner, that is the same as the current address ...
        businessPartner.setAddressChecksum(HashUtils.hash((AddressDTO.of(address).toString())));
        
        //... and address was already confirmed
        businessPartner.setAddressConfirmationState(CONFIRMED);
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is never called ...
        verify(notificationServiceMock, never()).sendMail(null);

        //... and Address Confirmation State is still Confirmed
        assertThat("Address Confirmation State after processing",
                businessPartner.getAddressConfirmationState(), 
                is(equalTo(CONFIRMED)));
    }
    
    /**
     * Business Partner is not a customer
     */
    @Test
    public void testNoCustomerEvent() {
        // Given dummy entity ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getNoCustomerBusinessPartner());
        
        // ... and a business partner service's mock that returns dummy entities ...        
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is never called ...
        verify(notificationServiceMock, never()).sendMail(null);
        
        //... and cipherService is never called
        verify(cipherMock, never()).encrypt(null);
        
        //... and address was never fetched
        verify(businessPartnerServiceMock,never()).getAddress(null);
    }
    
    /**
     * Business Partner is a person 
     */
    @Test
    public void testCustomerPersonEvent() {
        // Given dummy entity ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getCustomerAndPersonBusinessPartner());
        
        // ... and a business partner service's mock that returns dummy entities ...        
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is never called ...
        verify(notificationServiceMock, never()).sendMail(null);
        
        // ... and cipherService is never called
        verify(cipherMock, never()).encrypt(null);
        
        // ... and address was never fetched
        verify(businessPartnerServiceMock,never()).getAddress(null);
    }
    
    /**
     * Business Partner has no address
     */
    @Test
    public void testNoAddressBupa() {
        // Given dummy entities ...
        final CustomBusinessPartner businessPartner = CustomBusinessPartner.of(EntitySupplier.getDefaultBusinessPartnerRoot());
        final BusinessPartnerAddress address = null;
        
        // ... and a business partner service's mock that returns dummy entities        
        doReturn(businessPartner).when(businessPartnerServiceMock).getBusinessPartnerRootByKey(businessPartner.getBusinessPartner());
        doReturn(address).when(businessPartnerServiceMock).getAddress(businessPartner);
        
        // When
        testee.confirmAddress(businessPartner.getBusinessPartner());
        
        // Then notification service is never called ...
        verify(notificationServiceMock, never()).sendMail(null);
        
        // ... and Address Checksum is empty ...
        assertThat("Address Checksum after processing", 
                businessPartner.getAddressChecksum(),
                is(isEmptyOrNullString()));
        
        // ... and Address Confirmation State is empty
        assertThat("Confirmation State", 
                businessPartner.getAddressConfirmationState(),
                is(INITIAL));
    }
    
}
