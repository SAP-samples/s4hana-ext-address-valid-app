package com.sap.cloud.s4hana.eventing.security;

import java.io.Serializable;

import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

/**
 * Stores keys of {@link BusinessPartnerAddress} and maximal validity date.<br>
 * Authorizes not authenticated users to access open REST endpoints.
 * 
 * @see RSACipher
 * @see WebSecurityConfig
 */
public final class AddressConfirmationToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String businessPartner; 
	private final String addressID;
	private final long expiryTimeMilis;

	private AddressConfirmationToken(BusinessPartnerAddress address, int numberOfDaysValid) {
		super();
		this.businessPartner = address.getBusinessPartner();
		this.addressID = address.getAddressID();
		this.expiryTimeMilis = System.currentTimeMillis() + numberOfDaysValid * 24 * 60 * 60 * 1000;
	}
	
	/**
	 * @param address
	 *            {@link BusinessPartnerAddress} to be confirmed
	 * @param numberOfDaysValid
	 *            number of days during which the token be valid from the moment
	 *            of its creation
	 * 
	 * @see AddressConfirmationToken#isValid()
	 */
	public static final AddressConfirmationToken of(BusinessPartnerAddress address, int numberOfDaysValid) {
		return new AddressConfirmationToken(address, numberOfDaysValid);
	}

	/**
	 * @see BusinessPartnerAddress#getBusinessPartner()
	 */
	public String getBusinessPartner() {
		return businessPartner;
	}

	/**
	 * @see BusinessPartnerAddress#getAddressID()
	 */
	public String getAddressID() {
		return addressID;
	}

	/**
	 * @see System#currentTimeMillis()
	 */
	public long getExpiryTimeMilis() {
		return expiryTimeMilis;
	}
	
	/**
	 * @return {@code true} if the token did not expire yet
	 * 
	 * @see System#currentTimeMillis()
	 */
	public boolean isValid() {
		return System.currentTimeMillis() < expiryTimeMilis;
	}

}
