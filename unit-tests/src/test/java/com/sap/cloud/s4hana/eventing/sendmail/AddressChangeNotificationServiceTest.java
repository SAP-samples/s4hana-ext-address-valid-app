package com.sap.cloud.s4hana.eventing.sendmail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotification;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotificationService;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AddressChangeNotificationServiceTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	private Session sessionMock;
	
	@Mock
	private Configuration templateConfigurationMock;
	
	@Mock
	private Template templateMock;
	
	@InjectMocks
	public AddressChangeNotificationService notificationService;
	
	@Mock
	private Transport transportMock;
	
	@Captor
	private ArgumentCaptor<Message> emailCaptor;
	
	@Captor
	private ArgumentCaptor<Address[]> recipientsCaptor;
	
	@Test
	public void testSendMail() throws MessagingException, IOException, TemplateException {
		// Given business partner, address, contact and other necessary data
		final BusinessPartner bp = EntitySupplier.getDefaultBusinessPartner();
		final BusinessPartnerAddress address = EntitySupplier.getDefaultAddress();
		final BusinessPartner contact = EntitySupplier.getDefaultContactBusinessPartner();
		final String emailAddress = EntitySupplier.getDefaultContact().getEmailAddress();
		final String confirmationLink = "http://example.com/confirmAddress";
		
		// ... and a notification constructed from that data ...
		final AddressChangeNotification notification = AddressChangeNotification.of(
				bp, 
				AddressDTO.of(address), 
				contact,
				emailAddress,
				confirmationLink);
		
		// ... and mocked Session object ...
		when(sessionMock.getTransport()).thenReturn(transportMock);
		when(sessionMock.getProperties()).thenReturn(new Properties());
		
		// ... and mocked Template configuration ...
		when(templateConfigurationMock.getTemplate(AddressChangeNotificationService.TEMPLATE_FILE))
			.thenReturn(templateMock);
		
		// When notification email is sent
		notificationService.sendMail(notification);
		
		// Then the correct template is retrieved ...
		verify(templateConfigurationMock).getTemplate(AddressChangeNotificationService.TEMPLATE_FILE);
		
		// ... and the template is processed with the notification ...
		verify(templateMock).process(eq(notification), any());
		
		// ... and the email is sent ...
		verify(transportMock).sendMessage(emailCaptor.capture(), recipientsCaptor.capture());
		
		// ... to the same recipients that are specified in the email ...  
		final Message email = emailCaptor.getValue();
		final Address[] recipients = recipientsCaptor.getValue();		
		assertThat("recipients passed to Transport should be the same as recipients in email object", 
				Arrays.asList(recipients), containsInAnyOrder(email.getAllRecipients()));
		
		assertThat("email content", email.getContent(), is(instanceOf(String.class)));
		
		assertThat("email subject", 
				email.getSubject(), 
				containsString(bp.getBusinessPartnerFullName()));
	    assertTrue(true);
	}
	
}