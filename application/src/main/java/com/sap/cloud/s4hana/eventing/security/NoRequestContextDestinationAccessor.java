package com.sap.cloud.s4hana.eventing.security;

import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.GenericDestination;

@Component
public class NoRequestContextDestinationAccessor {
	
	@ExecuteWithJwtTokenFromXsuaa(error = "Cannot get destination using JWT token from XSUAA service")
	public GenericDestination getGenericDestination(final String destinationName) {
		return DestinationAccessor.getGenericDestination(destinationName);
	}

}
