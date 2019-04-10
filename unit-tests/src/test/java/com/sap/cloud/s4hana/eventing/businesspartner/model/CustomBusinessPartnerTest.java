package com.sap.cloud.s4hana.eventing.businesspartner.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.stream.IntStream;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;

public class CustomBusinessPartnerTest {
	
	/**
	 * Run test 100 times since {@link ModelMapper} with default settings produces not
	 * idempotent results
	 */
	@Test
	public void test100Of() {
		IntStream.range(0, 100).forEach(x -> testOf());
	}
	
	public void testOf() {
		final BusinessPartner businessPartner = EntitySupplier.getDefaultBusinessPartner();
		
		final CustomBusinessPartner customBusinessPartner = CustomBusinessPartner.of(businessPartner);
		
		assertThat("CustomBusinessPartner should be equal to original BusinessPartner", customBusinessPartner, is(equalTo(businessPartner)));
	}
	
	@Test
	public void testCustomFieldAddressChecksum() {
		final CustomBusinessPartner testee = new CustomBusinessPartner();
		
		testee.setAddressChecksum(EntitySupplier.ADDRESS_CHECKSUM);
		
		assertThat("testee.getAddressChecksum()", 
				testee.getAddressChecksum(), 
				is(EntitySupplier.ADDRESS_CHECKSUM));
		assertThat("testee.getCustomField(CustomBusinessPartner.ADDRESS_CHECKSUM)", 
				testee.getCustomField(CustomBusinessPartner.ADDRESS_CHECKSUM), 
				is(EntitySupplier.ADDRESS_CHECKSUM));
	}
	
	@Test
	public void testCustomFieldAddressConfirmationState() {
		final CustomBusinessPartner testee = new CustomBusinessPartner();
		
		assertThat("testee.getAddressConfirmationState() should return null when custom field is null", 
				testee.getAddressConfirmationState(), 
				is(nullValue()));
		
		testee.setAddressConfirmationState(EntitySupplier.ADDRESS_CONFIRMATION_STATE);
		
		// getter returns enum
		assertThat("testee.getAddressConfirmationState()", 
				testee.getAddressConfirmationState(), 
				is(EntitySupplier.ADDRESS_CONFIRMATION_STATE));
		
		// getCustomField returns enum's description value
		assertThat("testee.getCustomField(CustomBusinessPartner.ADDRESS_CONFIRMATION_STATE)", 
				testee.getCustomField(CustomBusinessPartner.ADDRESS_CONFIRMATION_STATE), 
				is(EntitySupplier.ADDRESS_CONFIRMATION_STATE.toString()));
	}
	
}
