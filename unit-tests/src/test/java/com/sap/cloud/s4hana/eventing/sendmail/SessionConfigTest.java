package com.sap.cloud.s4hana.eventing.sendmail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.URLName;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;
import com.sap.cloud.s4hana.eventing.security.NoRequestContextDestinationAccessor;
import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

public class SessionConfigTest {
	
	@ClassRule
	public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
	
	SessionConfig testee;
	
	@Before
	public void setUp() {
		testee = new SessionConfig(new NoRequestContextDestinationAccessor(), null);
	}

	@Test
	public void testGetDefaultSession() throws UnknownHostException {
		// When
		final Session session = testee.getDefaultSession();
		
		// Then
		assertThat("Session", session, is(not(nullValue())));
		
		assertThat(Email.DESTINATION_PROPERTY_FROM + " property of Session", 
				session.getProperty(Email.DESTINATION_PROPERTY_FROM),
				is(CloudFoundryEnvironmentMock.DefaultMailDestination.FROM));
		
		final URLName smtpsUrlName = new URLName(CloudFoundryEnvironmentMock.DefaultMailDestination.SMTPS_HOST);
		final PasswordAuthentication passwordAuthentication = session.requestPasswordAuthentication(
				InetAddress.getByName(smtpsUrlName.getHost()), smtpsUrlName.getPort(), smtpsUrlName.getProtocol(), "", smtpsUrlName.getUsername());
		assertThat("user", passwordAuthentication.getPassword(), is(CloudFoundryEnvironmentMock.DefaultMailDestination.PASSWORD));
		assertThat("password", passwordAuthentication.getUserName(), is(CloudFoundryEnvironmentMock.DefaultMailDestination.USER));
	}
	
	@Test(expected = DestinationNotFoundException.class)
	public void testGetSessionFromNonExistingDestination() {
		testee.getSession("Not existing destination name");
	}
	
	@Test(expected = SAPMailingException.class)
	public void testGetSessionFromHttpDestination() {
		// Given an ERP destination name
		final String erpDestinationName = environmentMock.getErpDestinationMock().getName();
		
		// When get session is called with an ERP destination name (which is not of type MAIL} 
		testee.getSession(erpDestinationName);
		
		// Then an exception is thrown
	}
	
}
