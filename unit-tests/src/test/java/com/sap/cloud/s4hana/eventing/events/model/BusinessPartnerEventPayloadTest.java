package com.sap.cloud.s4hana.eventing.events.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.sap.cloud.s4hana.eventing.testutil.ValidationUtil;

public class BusinessPartnerEventPayloadTest {
	
	@Test
	public void testValidWhenOneKey() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		assertThat("Violated properties", violations, is(empty()));
	}
	
	@Test
	public void testInvalidWhenKeysAreNull() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		testee.keys = null;
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);

		assertThat("Violated properties", violations, containsInAnyOrder("keys"));
	}
	
	@Test
	public void testInvalidWhenKeysAreEmpty() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		testee.keys = Collections.emptyList();
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		assertThat("Violated properties", violations, containsInAnyOrder("keys"));
	}
	
	@Test
	public void testInvalidWhenMoreThanOneKey() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		final BusinessPartnerEvent.Payload.BusinessPartnerKey key = testee.keys.iterator().next();
		testee.keys = Arrays.asList(key, key);
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		assertThat("Violated properties", violations, containsInAnyOrder("keys"));
	}
	
	@Test
	public void testInvalidWhenSingleKeyIsNull() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		testee.keys = Arrays.asList((BusinessPartnerEvent.Payload.BusinessPartnerKey) null);
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		assertThat("Violated properties", violations, containsInAnyOrder("keys[].<iterable element>"));
	}
	
	@Test
	public void testInvalidWhenBusinessPartnerIsNull() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		final BusinessPartnerEvent.Payload.BusinessPartnerKey key = testee.keys.iterator().next();
		key.businessPartnerKey = null;
		testee.keys = Arrays.asList(key);
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		assertThat("Violated properties", violations, containsInAnyOrder("keys[0].businessPartnerKey"));
	}
	
	@Test
	public void testValid() {
		final BusinessPartnerEvent.Payload testee = givenValidBusinessPartnerEventPayload();
		
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		assertThat("Violated properties", violations, is(empty()));
	}
	
	public static BusinessPartnerEvent.Payload givenValidBusinessPartnerEventPayload() {
		final BusinessPartnerEvent.Payload result = new BusinessPartnerEvent.Payload();
		
		final BusinessPartnerEvent.Payload.BusinessPartnerKey key = new BusinessPartnerEvent.Payload.BusinessPartnerKey();
		key.businessPartnerKey = "0123456789";
		result.keys = Collections.singleton(key);
		
		return result;
	}
	
}
