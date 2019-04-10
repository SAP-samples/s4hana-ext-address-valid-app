package com.sap.cloud.s4hana.eventing.businesspartner.model;

import com.sap.cloud.s4hana.eventing.core.util.MapperUtils;
import com.sap.cloud.sdk.s4hana.datamodel.odata.exception.NoSuchEntityFieldException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.field.BusinessPartnerField;

/**
 * {@link BusinessPartner} extended with custom fields
 *
 */
public class CustomBusinessPartner extends BusinessPartner {
	
	public static final String ADDRESS_CONFIRMATION_STATE_CUSTOM_FIELD_NAME = "YY1_AddrConfState_bus";
	public static final String ADDRESS_CHECKSUM_CUSTOM_FIELD_NAME = "YY1_AddressChecksum_bus";
	
	public static final BusinessPartnerField<String> ADDRESS_CHECKSUM = 
			BusinessPartner.field(ADDRESS_CHECKSUM_CUSTOM_FIELD_NAME, String.class);
	public static final BusinessPartnerField<String> ADDRESS_CONFIRMATION_STATE = 
			BusinessPartner.field(ADDRESS_CONFIRMATION_STATE_CUSTOM_FIELD_NAME, String.class);
	
	public static CustomBusinessPartner newInstance() {
		return new CustomBusinessPartner();
	}
	
	public static CustomBusinessPartner of(BusinessPartner businessPartner) {
		return MapperUtils.map(businessPartner, CustomBusinessPartner.class);
	}
	
	/**
	 * @return address confirmation state or {@code null} if the state is invalid
	 */
	public AddrConfState getAddressConfirmationState() {
		try {
			String stringValue = getCustomField(ADDRESS_CONFIRMATION_STATE);
			return AddrConfState.of(stringValue);
		} catch (NoSuchEntityFieldException e) {
			return null;
		}
	}

	public void setAddressConfirmationState(AddrConfState addressConfirmationState) {
		if (addressConfirmationState == null) {
			setCustomField(ADDRESS_CONFIRMATION_STATE, null);
		} else {
			setCustomField(ADDRESS_CONFIRMATION_STATE, addressConfirmationState.toString());
		}
	}

	public String getAddressChecksum() {
		return getCustomField(ADDRESS_CHECKSUM);
	}

	public void setAddressChecksum(String addressChecksum) {
		setCustomField(ADDRESS_CHECKSUM, addressChecksum);
	}

}
