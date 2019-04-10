package com.sap.cloud.s4hana.eventing.businesspartner.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.sap.cloud.s4hana.eventing.core.util.MapperUtils;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

/**
 * Data Transfer Object (DTO) for {@link BusinessPartnerAddress} which is used
 * to calculate a hash value which is used to check whether an address was
 * changed.
 * <p>
 * It simply copies all fields from {@link BusinessPartnerAddress} that are
 * confirmed by a customer.
 *
 */
public class AddressDTO {

	/*
	 * All fields from BusinessPartnerAddress that are confirmed by a customer.
	 */
	
	private String addressID;
	
	private String businessPartner;

	private String authorizationGroup;

	private String additionalStreetPrefixName;

	private String additionalStreetSuffixName;

	private String addressTimeZone;

	private String careOfName;

	private String cityCode;

	private String cityName;

	private String companyPostalCode;

	private String country;

	private String county;

	private String deliveryServiceNumber;

	private String deliveryServiceTypeCode;

	private String district;

	private String formOfAddress;

	private String fullName;

	private String homeCityName;

	private String houseNumber;

	private String houseNumberSupplementText;

	private String language;

	private String pOBox;

	private String pOBoxDeviatingCityName;

	private String pOBoxDeviatingCountry;

	private String pOBoxDeviatingRegion;

	private Boolean pOBoxIsWithoutNumber;

	private String pOBoxLobbyName;

	private String pOBoxPostalCode;

	private String person;

	private String postalCode;

	private String prfrdCommMediumType;

	private String region;

	private String streetName;

	private String streetPrefixName;

	private String streetSuffixName;

	private String taxJurisdiction;

	private String transportZone;

	private String addressIDByExternalSystem;
	
	public static AddressDTO of(BusinessPartnerAddress businessPartnerAddress) {
		return MapperUtils.map(businessPartnerAddress, AddressDTO.class);
	}
	
	private AddressDTO() {
		// allow instantation using static methods only
	}
	
	/*
	 * All methods below are just getters and setters
	 */
	
	public String getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(String businessPartner) {
		this.businessPartner = businessPartner;
	}

	public String getAuthorizationGroup() {
		return authorizationGroup;
	}

	public void setAuthorizationGroup(String authorizationGroup) {
		this.authorizationGroup = authorizationGroup;
	}

	public String getAdditionalStreetPrefixName() {
		return additionalStreetPrefixName;
	}

	public void setAdditionalStreetPrefixName(String additionalStreetPrefixName) {
		this.additionalStreetPrefixName = additionalStreetPrefixName;
	}

	public String getAdditionalStreetSuffixName() {
		return additionalStreetSuffixName;
	}

	public void setAdditionalStreetSuffixName(String additionalStreetSuffixName) {
		this.additionalStreetSuffixName = additionalStreetSuffixName;
	}

	public String getAddressTimeZone() {
		return addressTimeZone;
	}

	public void setAddressTimeZone(String addressTimeZone) {
		this.addressTimeZone = addressTimeZone;
	}

	public String getCareOfName() {
		return careOfName;
	}

	public void setCareOfName(String careOfName) {
		this.careOfName = careOfName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCompanyPostalCode() {
		return companyPostalCode;
	}

	public void setCompanyPostalCode(String companyPostalCode) {
		this.companyPostalCode = companyPostalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getDeliveryServiceNumber() {
		return deliveryServiceNumber;
	}

	public void setDeliveryServiceNumber(String deliveryServiceNumber) {
		this.deliveryServiceNumber = deliveryServiceNumber;
	}

	public String getDeliveryServiceTypeCode() {
		return deliveryServiceTypeCode;
	}

	public void setDeliveryServiceTypeCode(String deliveryServiceTypeCode) {
		this.deliveryServiceTypeCode = deliveryServiceTypeCode;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getFormOfAddress() {
		return formOfAddress;
	}

	public void setFormOfAddress(String formOfAddress) {
		this.formOfAddress = formOfAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getHomeCityName() {
		return homeCityName;
	}

	public void setHomeCityName(String homeCityName) {
		this.homeCityName = homeCityName;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getHouseNumberSupplementText() {
		return houseNumberSupplementText;
	}

	public void setHouseNumberSupplementText(String houseNumberSupplementText) {
		this.houseNumberSupplementText = houseNumberSupplementText;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPOBox() {
		return pOBox;
	}

	public void setPOBox(String pOBox) {
		this.pOBox = pOBox;
	}

	public String getPOBoxDeviatingCityName() {
		return pOBoxDeviatingCityName;
	}

	public void setPOBoxDeviatingCityName(String pOBoxDeviatingCityName) {
		this.pOBoxDeviatingCityName = pOBoxDeviatingCityName;
	}

	public String getPOBoxDeviatingCountry() {
		return pOBoxDeviatingCountry;
	}

	public void setPOBoxDeviatingCountry(String pOBoxDeviatingCountry) {
		this.pOBoxDeviatingCountry = pOBoxDeviatingCountry;
	}

	public String getPOBoxDeviatingRegion() {
		return pOBoxDeviatingRegion;
	}

	public void setPOBoxDeviatingRegion(String pOBoxDeviatingRegion) {
		this.pOBoxDeviatingRegion = pOBoxDeviatingRegion;
	}

	public Boolean getPOBoxIsWithoutNumber() {
		return pOBoxIsWithoutNumber;
	}

	public void setPOBoxIsWithoutNumber(Boolean pOBoxIsWithoutNumber) {
		this.pOBoxIsWithoutNumber = pOBoxIsWithoutNumber;
	}

	public String getPOBoxLobbyName() {
		return pOBoxLobbyName;
	}

	public void setPOBoxLobbyName(String pOBoxLobbyName) {
		this.pOBoxLobbyName = pOBoxLobbyName;
	}

	public String getPOBoxPostalCode() {
		return pOBoxPostalCode;
	}

	public void setPOBoxPostalCode(String pOBoxPostalCode) {
		this.pOBoxPostalCode = pOBoxPostalCode;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPrfrdCommMediumType() {
		return prfrdCommMediumType;
	}

	public void setPrfrdCommMediumType(String prfrdCommMediumType) {
		this.prfrdCommMediumType = prfrdCommMediumType;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getStreetPrefixName() {
		return streetPrefixName;
	}

	public void setStreetPrefixName(String streetPrefixName) {
		this.streetPrefixName = streetPrefixName;
	}

	public String getStreetSuffixName() {
		return streetSuffixName;
	}

	public void setStreetSuffixName(String streetSuffixName) {
		this.streetSuffixName = streetSuffixName;
	}

	public String getTaxJurisdiction() {
		return taxJurisdiction;
	}

	public void setTaxJurisdiction(String taxJurisdiction) {
		this.taxJurisdiction = taxJurisdiction;
	}

	public String getTransportZone() {
		return transportZone;
	}

	public void setTransportZone(String transportZone) {
		this.transportZone = transportZone;
	}

	public String getAddressIDByExternalSystem() {
		return addressIDByExternalSystem;
	}

	public void setAddressIDByExternalSystem(String addressIDByExternalSystem) {
		this.addressIDByExternalSystem = addressIDByExternalSystem;
	}

	public String getAddressID() {
		return addressID;
	}

	public void setAddressID(String addressID) {
		this.addressID = addressID;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, true);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, true);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
