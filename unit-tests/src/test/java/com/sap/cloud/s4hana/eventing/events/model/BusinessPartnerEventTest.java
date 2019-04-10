package com.sap.cloud.s4hana.eventing.events.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;

import org.junit.Test;

import com.sap.cloud.s4hana.eventing.testutil.ValidationUtil;

public class BusinessPartnerEventTest extends BusinessEventTest {
	
	@Override
	public BusinessPartnerEvent newTestee() {
		return new BusinessPartnerEvent();
	}

	@Override
	public BusinessPartnerEvent makeValid(BusinessEvent<?> event) {
		super.makeValid(event);
		
		if (!(event instanceof BusinessPartnerEvent)) {
			throw new IllegalArgumentException("event is not an instance of " + BusinessPartnerEvent.class.getSimpleName());
		}
		final BusinessPartnerEvent businessPartnerEvent = (BusinessPartnerEvent) event;
		
		final BusinessPartnerEvent.Payload payload = BusinessPartnerEventPayloadTest.givenValidBusinessPartnerEventPayload();
		businessPartnerEvent.setPayload(payload);
		
		return businessPartnerEvent;
	}
	
	/**
	 * Tests @{@link javax.validation.constraints.NotNull} annotation on
	 * {@link BusinessPartnerEvent#getPayload()} method
	 */
	@Test
	public void testWithNullPayload() {
		// Given a testee business event object...
		final BusinessPartnerEvent testee = makeValid(newTestee());
		
		//...that has an incorrect payload value
		testee.setPayload(null);
		
		// When the object is validated
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);

		// Then it has a violation of the payload property
		assertThat("Violated properties", violations, containsInAnyOrder("payload"));
	}
	
	/**
	 * Tests @{@link javax.validation.Valid} annotation on
	 * {@link BusinessPartnerEvent#getPayload()} method
	 */
	@Test
	public void testWithInvalidPayload() {
		// Given a testee business event object...
		final BusinessPartnerEvent testee = makeValid(newTestee());
		
		//...that has an incorrect payload value
		testee.getPayload().keys = null;
		
		// When the object is validated
		final Collection<String> violations = ValidationUtil.getViolatedPropertyPaths(testee);
		
		// Then it has a violation of the payload property
		assertThat("Violated properties", violations, containsInAnyOrder("payload.keys"));
	}

}
