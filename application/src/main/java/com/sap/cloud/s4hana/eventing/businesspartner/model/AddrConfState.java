package com.sap.cloud.s4hana.eventing.businesspartner.model;

import java.util.stream.Stream;

public enum AddrConfState {    
    
	/**
	 * Address is changed/created but no email with confirmation link is sent
	 */
	INITIAL("Initial"), 
    
    /**
	 * Email with confirmation link is sent but the address is not yet confirmed
	 */
    OPEN("Open"), 
    
    /**
     * Address is confirmed
     */
    CONFIRMED("Confirmed");
    
    private final String value;
    
    /**
	 * @return address confirmation state with {@code value}.<br>
	 *         If there is no address confirmation state with {@code value} then
	 *         {@link AddrConfState#INITIAL} is returned
	 */
    public static AddrConfState of(String value) {
		return Stream.of(AddrConfState.values())
				.filter(state -> state.toString().equals(value))
				.findAny()
				.orElse(INITIAL);
    }
    
    AddrConfState(String value) {
       this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
}
