package com.sap.cloud.s4hana.eventing.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPSecurityException;

public class RSACipherTest {

	@Test
	public void testLogAndWrap() {
		final String expectedMessage = "Expected error message";
		final RuntimeException expectedCauseException = new RuntimeException("Expected error");
		
		final SAPSecurityException wrappedException = RSACipher.logAndWrap(expectedMessage, expectedCauseException);
		
		assertThat("expected exception", 
				wrappedException,
				allOf(instanceOf(SAPSecurityException.class),
						hasProperty("message", is(expectedMessage)),
						hasProperty("cause", is(expectedCauseException))));
	}
	
	@Test
	public void testGogAndWrapNoSuchAlgorithmException() {
		final String algorithm = "Expected algorithm"; 
		final NoSuchAlgorithmException expectedCauseException = new NoSuchAlgorithmException("Message doesn't matter");
		
		final SAPSecurityException wrappedException = RSACipher.logAndWrap(expectedCauseException, algorithm);
		
		assertThat("expected exception", 
				wrappedException,
				allOf(instanceOf(SAPSecurityException.class),
						hasProperty("message", containsString(algorithm + " algorithm doesn't exist")),
						hasProperty("cause", is(expectedCauseException))));		
	}
	
	@Test
	public void testRemoveExtraCharactersFromPublicKey() {
		final String algorithm = "Expected algorithm"; 
		final String expectedResult = "AllWhitespacesAreRemoved";
		final String initialString = "-----BEGIN PUBLIC KEY-----    All   Whitespaces    Are Removed    -----END PUBLIC KEY-----";
		
		final String result = RSACipher.removeExtraCharacters(initialString, algorithm, /* isPrivateKey = */ false);
		
		assertThat("result", result, is(expectedResult));
	}
	
	@Test
	public void testRemoveExtraCharactersFromPrivateKey() {
		final String algorithm = "Expected algorithm";
		final String expectedResult = "AllWhitespacesAreRemoved";
		final String initialString = String.format("-----BEGIN %s PRIVATE KEY-----    All   Whitespaces    Are Removed    -----END %<s PRIVATE KEY-----", algorithm);
		
		final String result = RSACipher.removeExtraCharacters(initialString, algorithm, /* isPrivateKey = */ true);
		
		assertThat("result", result, is(expectedResult));
	}
	
}
