package com.sap.cloud.s4hana.eventing.security;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;

public class HashUtilsTest {
	
	@Test
	public void testHash() {
		// given two objects which state is the same
		final String testee1 = new String("Some string value that will be the same for each Testee instance");
		final String testee2 = new String("Some string value that will be the same for each Testee instance");
		
		// when the hash() is called
		final String hash1 = HashUtils.hash(testee1);
		final String hash2 = HashUtils.hash(testee2);
		
		// then hashes are equal
		assertThat("Hashes of objects with the same state should be equal", hash1.equals(hash2));
	}
	
	@Test
	public void testHashWithAddress() {
	    final AddressDTO address1 = AddressDTO.of(EntitySupplier.getDefaultAddress());
	    final AddressDTO address2 = AddressDTO.of(EntitySupplier.getDefaultAddress());
	    
	    // when the hash() is called
	    final String hash1 = HashUtils.hash(address1.toString());
	    final String hash2 = HashUtils.hash(address2.toString());
	    
	    // then hashes are equal
	    assertThat("Hashes of objects with the same state should be equal", hash1.equals(hash2));
	     
	}
	
}
