package com.sap.cloud.s4hana.eventing.security;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;

public class CloudRSACipherTest extends RSACipherImplTester {
	
	@ClassRule
	public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
	
	CloudRSACipher testee;
	
	@Override
	CloudRSACipher getTestee() {
		return testee;
	}
	
	@Before
	public void setUp() {
		testee = new CloudRSACipher(new NoRequestContextDestinationAccessor(), null);
	}
	
	@Test
	public void testGetKeyPairFromDestination() {
		assertKeyPair(testee.getKeyPairFromDestination(CloudRSACipher.DEFAULT_DESTINATION_NAME, CloudRSACipher.ALGORITHM),
				CloudRSACipher.ALGORITHM);
	}
	
}
