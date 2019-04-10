package com.sap.cloud.s4hana.eventing.sendmail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class EmailTest {
	
	private static final String INVALID_ADDRESS = "invalid@because@two.ats";
	private static final String NOT_AN_EMAIL_ADDRESS = "not a correct email address";
	private static final String EXAMPLE_EMAIL_ADDRESS = "exampleEmailAddress@example.com";
	private static final String EXAMPLE_EMAIL_ADDRESS_WITH_ADDITIONAL_INFO = "ExampleAddress <exampleAddressWithInfo@example.com>";

	@Mock
	public Session sessionMock;
	
	Email testee;
	
	public void givenTesteeEmail() {
		doReturn(new Properties()).when(sessionMock).getProperties();
		testee = Email.forSession(sessionMock);
	}
	
	@Test
	public void testForSession() {
		doReturn("from@example.com").when(sessionMock).getProperty(Email.DESTINATION_PROPERTY_FROM);

		givenTesteeEmail();
		
		assertThat(testee, is(not(nullValue())));
		verify(sessionMock).getProperty(Email.DESTINATION_PROPERTY_FROM);
	}
	
	@Test
	public void testForSessionWhenDestinationPropertyFromIsNotFoundThenEmailIsStillCreated() {
		doThrow(new RuntimeException()).when(sessionMock).getProperty(Email.DESTINATION_PROPERTY_FROM);
		
		givenTesteeEmail();
		
		assertThat(testee, is(not(nullValue())));
		verify(sessionMock).getProperty(Email.DESTINATION_PROPERTY_FROM);
	}
	
	@Test
	public void testForSessionWhenFromIsNotCorrectThenEmailIsStillCreated() {
		doReturn(NOT_AN_EMAIL_ADDRESS).when(sessionMock).getProperty(Email.DESTINATION_PROPERTY_FROM);
		
		givenTesteeEmail();
		
		assertThat(testee, is(not(nullValue())));
		verify(sessionMock).getProperty(Email.DESTINATION_PROPERTY_FROM);
	}
	
	@Test
	public void testFrom() throws MessagingException {
		testFrom(EXAMPLE_EMAIL_ADDRESS_WITH_ADDITIONAL_INFO);
	}
	
	@Test
	public void testFromSimpleAddress() throws MessagingException {
		testFrom(EXAMPLE_EMAIL_ADDRESS);
	}
	
	@Test(expected = SAPMailingException.class)
	public void testFromSimpleAddressWhenNotAnEmailAddressThenException() throws MessagingException {
		testFrom(NOT_AN_EMAIL_ADDRESS);
	}
	
	@Test(expected = SAPMailingException.class)
	public void testFromSimpleAddressWhenInvalidThenException() throws MessagingException {
		testFrom(INVALID_ADDRESS);
	}
	
	public void testFrom(final String expectedAddress) throws MessagingException {
		givenTesteeEmail();
		
		testee.from(expectedAddress);
		
		final Address[] from = testee.getMimeMessage().getFrom();
		assertThat("there should be only one 'from' address", from.length, is(1));
		assertThat("from address", from[0].toString(), is(expectedAddress));
	}
	
	@Test
	public void testTo() throws MessagingException {
		testFrom(EXAMPLE_EMAIL_ADDRESS_WITH_ADDITIONAL_INFO);
	}
	
	@Test
	public void testToSimpleAddress() throws MessagingException {
		testTo(EXAMPLE_EMAIL_ADDRESS);
	}
	
	@Test(expected = SAPMailingException.class)
	public void testToSimpleAddressWhenNotAnEmailAddressThenException() throws MessagingException {
		testTo(NOT_AN_EMAIL_ADDRESS);
	}
	
	@Test(expected = SAPMailingException.class)
	public void testToSimpleAddressWhenInvalidThenException() throws MessagingException {
		testTo(INVALID_ADDRESS);
	}
	
	public void testTo(final String expectedAddress) throws MessagingException {
		givenTesteeEmail();
		
		testee.to(expectedAddress);
		
		final Address[] to = testee.getMimeMessage().getRecipients(RecipientType.TO);
		assertThat("there should be only one 'to' address", to.length, is(1));
		assertThat("to address", to[0].toString(), is(expectedAddress));
	}
	
	@Test
	public void testSubject() throws MessagingException {
		givenTesteeEmail();
		final String expectedSubject = "Expected subject";
		
		testee.subject(expectedSubject);
		
		assertThat("subject", testee.getMimeMessage().getSubject(), is(expectedSubject));
	}
	
	@Test
	public void testBody() throws MessagingException, IOException {
		givenTesteeEmail();
		final String expectedBody = "Expected body \n multi-line should also be ok";
		
		testee.body(expectedBody);
		
		assertThat("body", testee.getMimeMessage().getContent(), is(expectedBody));
	}
	
	@Mock
	Transport transportMock;
	
	@Test
	public void testSend() throws MessagingException {
		doReturn(transportMock).when(sessionMock).getTransport();
		givenTesteeEmail();
		
		testee.send();
		
		verify(sessionMock).getTransport();
		verify(transportMock).connect();
		verify(transportMock).sendMessage(testee.getMimeMessage(), testee.getMimeMessage().getAllRecipients());
	}
	
	@Test
	public void testParseEmailAddresses() throws MessagingException {
		testParseEmailAddresses(EXAMPLE_EMAIL_ADDRESS_WITH_ADDITIONAL_INFO, EXAMPLE_EMAIL_ADDRESS);
	}
	
	@Test(expected = AddressException.class)
	public void testParseEmailAddressesWhenInvalidAddressThenNoException() throws MessagingException {
		testParseEmailAddresses(EXAMPLE_EMAIL_ADDRESS, INVALID_ADDRESS);
	}
	
	@Test(expected = AddressException.class)
	public void testParseEmailAddressesWhenNoAddressThenException() throws MessagingException {
		testParseEmailAddresses("");
	}
	
	public void testParseEmailAddresses(final String... expectedAddress) throws MessagingException {
		final InternetAddress[] address = Email.parseEmailAddresses(Stream.of(expectedAddress).collect(Collectors.joining(",")));
		
		final List<String> addressToString = Stream.of(address)
				.map(Objects::toString)
				.collect(Collectors.toList());
		assertThat("parsed addresses", addressToString, containsInAnyOrder(expectedAddress));
	}
	
	@Test
	public void testParseEmailAddress() throws MessagingException {
		testParseEmailAddress(EXAMPLE_EMAIL_ADDRESS);
	}
	
	@Test
	public void testParseEmailAddressWithAdditionalInfo() throws MessagingException {
		testParseEmailAddress(EXAMPLE_EMAIL_ADDRESS_WITH_ADDITIONAL_INFO);
	}
	
	@Test(expected = AddressException.class)
	public void testParseEmailAddressWhenInvalidAddressThenNoException() throws MessagingException {
		testParseEmailAddress(EXAMPLE_EMAIL_ADDRESS + "," + EXAMPLE_EMAIL_ADDRESS_WITH_ADDITIONAL_INFO);
	}
	
	@Test(expected = AddressException.class)
	public void testParseEmailAddressWhenNoAddressThenException() throws MessagingException {
		testParseEmailAddress("");
	}
	
	public void testParseEmailAddress(String expectedAddress) throws MessagingException {
		final InternetAddress addresses = Email.parseEmailAddress(expectedAddress);
		assertThat("parsed address", addresses.toString(), is(expectedAddress));
	}
	
	/**
	 * Learning tests for InternetAddress.parse(addresses)
	 * 
	 */
	
	@Test(expected = NullPointerException.class)
	public void testParseEmailAddressesWithNullStringThrowsNPE() throws AddressException {
		InternetAddress.parse(null);
	}
	
	@Test
	public void testParseEmailAddressesWithEmptyStringDoesNotThrowException() throws AddressException {
		// When InternetAddress.parse is called with empty string 
		final InternetAddress[] parsedAddresses = InternetAddress.parse("");
		
		// Then the result is an empty array and no exception is thrown
		assertThat("parsedAddresses", parsedAddresses, is(emptyArray()));
	}
	
	@Test
	public void testParseEmailAddressesWithWhitespaceStringDoesNotThrowException() throws AddressException {
		// When InternetAddress.parse is called with string with whitespaces 
		final InternetAddress[] parsedAddresses = InternetAddress.parse("            ");
		
		// Then the result is an empty array and no exception is thrown
		assertThat("parsedAddresses", parsedAddresses, is(emptyArray()));
	}
	
	@Test(expected = AddressException.class)
	public void testParseEmailAddressesWithInvalidAddressThrowsException() throws AddressException {
		InternetAddress.parse("@@invalid address");
	}

}