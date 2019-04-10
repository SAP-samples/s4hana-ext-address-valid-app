package com.sap.cloud.s4hana.eventing.events.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;

import org.junit.Test;

import com.sap.cloud.s4hana.eventing.testutil.ValidationUtil;

public abstract class AbstractBusinessEventTest<T extends BusinessEvent<?>> {
	
	public abstract T newTestee();
	
	public abstract T makeValid(T event);
	
	@Test
	public void testContentTypeRegexpWithCorrectValue() {
		// Given a testee business event object...
		final BusinessEvent<?> testee = makeValid(newTestee());
		
		//...that has a correct content-type value
		testee.setContentType("application/json");
		
		// When the object is validated
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		// Then it has no violations
		assertThat("Violated properties", violations, is(empty()));
	}
	
	@Test
	public void testContentTypeRegexpWithNullValue() {
		// Given a testee business event object...
		final BusinessEvent<?> testee = makeValid(newTestee());
		
		// ...that has a null content-type value which is considered valid by
		// the javax.validation.Regexp validation constrain
		testee.setContentType(null);
		
		// When the object is validated
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		// Then it has no violations
		assertThat("Violated properties", violations, is(empty()));
	}
	
	@Test
	public void testContentTypeRegexpWithIncorrectValue() {
		// Given a testee business event object...
		final BusinessEvent<?> testee = makeValid(newTestee());
		
		//...that has an incorrect content-type value
		testee.setContentType("Not application json");
		
		// When the object is validated
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		// Then it has a single violation for contentType property
		assertThat("Violated properties", violations, containsInAnyOrder("contentType"));
	}
	
}
