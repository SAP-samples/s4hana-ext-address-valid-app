package com.sap.cloud.s4hana.eventing.businesspartner.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.sap.cloud.s4hana.eventing.core.util.MapperUtils;
import com.sap.cloud.s4hana.eventing.testutil.EntitySupplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

public class AddressDTOTest {
	
	@Test
	public void testAddressDTOSetsAllFieldsFromBusinessPartnerAddress() {
		final BusinessPartnerAddress businessPartnerAddress = EntitySupplier.getDefaultAddress();
		
		final AddressDTO testee = AddressDTO.of(businessPartnerAddress);
		
		assertThat("businessPartner", testee.getBusinessPartner(), 
			is(businessPartnerAddress.getBusinessPartner()));
		assertThat("authorizationGroup", testee.getAuthorizationGroup(), 
			is(businessPartnerAddress.getAuthorizationGroup()));
		assertThat("additionalStreetPrefixName", testee.getAdditionalStreetPrefixName(), 
			is(businessPartnerAddress.getAdditionalStreetPrefixName()));
		assertThat("additionalStreetSuffixName", testee.getAdditionalStreetSuffixName(), 
			is(businessPartnerAddress.getAdditionalStreetSuffixName()));
		assertThat("addressTimeZone", testee.getAddressTimeZone(), 
			is(businessPartnerAddress.getAddressTimeZone()));
		assertThat("careOfName", testee.getCareOfName(), 
			is(businessPartnerAddress.getCareOfName()));
		assertThat("cityCode", testee.getCityCode(), 
			is(businessPartnerAddress.getCityCode()));
		assertThat("cityName", testee.getCityName(), 
			is(businessPartnerAddress.getCityName()));
		assertThat("companyPostalCode", testee.getCompanyPostalCode(), 
			is(businessPartnerAddress.getCompanyPostalCode()));
		assertThat("country", testee.getCountry(), 
			is(businessPartnerAddress.getCountry()));
		assertThat("county", testee.getCounty(), 
			is(businessPartnerAddress.getCounty()));
		assertThat("deliveryServiceNumber", testee.getDeliveryServiceNumber(), 
			is(businessPartnerAddress.getDeliveryServiceNumber()));
		assertThat("deliveryServiceTypeCode", testee.getDeliveryServiceTypeCode(), 
			is(businessPartnerAddress.getDeliveryServiceTypeCode()));
		assertThat("district", testee.getDistrict(), 
			is(businessPartnerAddress.getDistrict()));
		assertThat("formOfAddress", testee.getFormOfAddress(), 
			is(businessPartnerAddress.getFormOfAddress()));
		assertThat("fullName", testee.getFullName(), 
			is(businessPartnerAddress.getFullName()));
		assertThat("homeCityName", testee.getHomeCityName(), 
			is(businessPartnerAddress.getHomeCityName()));
		assertThat("houseNumber", testee.getHouseNumber(), 
			is(businessPartnerAddress.getHouseNumber()));
		assertThat("houseNumberSupplementText", testee.getHouseNumberSupplementText(), 
			is(businessPartnerAddress.getHouseNumberSupplementText()));
		assertThat("language", testee.getLanguage(), 
			is(businessPartnerAddress.getLanguage()));
		assertThat("pOBox", testee.getPOBox(), 
			is(businessPartnerAddress.getPOBox()));
		assertThat("pOBoxDeviatingCityName", testee.getPOBoxDeviatingCityName(), 
			is(businessPartnerAddress.getPOBoxDeviatingCityName()));
		assertThat("pOBoxDeviatingCountry", testee.getPOBoxDeviatingCountry(), 
			is(businessPartnerAddress.getPOBoxDeviatingCountry()));
		assertThat("pOBoxDeviatingRegion", testee.getPOBoxDeviatingRegion(), 
			is(businessPartnerAddress.getPOBoxDeviatingRegion()));
		assertThat("pOBoxIsWithoutNumber", testee.getPOBoxIsWithoutNumber(), 
			is(businessPartnerAddress.getPOBoxIsWithoutNumber()));
		assertThat("pOBoxLobbyName", testee.getPOBoxLobbyName(), 
			is(businessPartnerAddress.getPOBoxLobbyName()));
		assertThat("pOBoxPostalCode", testee.getPOBoxPostalCode(), 
			is(businessPartnerAddress.getPOBoxPostalCode()));
		assertThat("person", testee.getPerson(), 
			is(businessPartnerAddress.getPerson()));
		assertThat("postalCode", testee.getPostalCode(), 
			is(businessPartnerAddress.getPostalCode()));
		assertThat("prfrdCommMediumType", testee.getPrfrdCommMediumType(), 
			is(businessPartnerAddress.getPrfrdCommMediumType()));
		assertThat("region", testee.getRegion(), 
			is(businessPartnerAddress.getRegion()));
		assertThat("streetName", testee.getStreetName(), 
			is(businessPartnerAddress.getStreetName()));
		assertThat("streetPrefixName", testee.getStreetPrefixName(), 
			is(businessPartnerAddress.getStreetPrefixName()));
		assertThat("streetSuffixName", testee.getStreetSuffixName(), 
			is(businessPartnerAddress.getStreetSuffixName()));
		assertThat("taxJurisdiction", testee.getTaxJurisdiction(), 
			is(businessPartnerAddress.getTaxJurisdiction()));
		assertThat("transportZone", testee.getTransportZone(), 
			is(businessPartnerAddress.getTransportZone()));
		assertThat("addressIDByExternalSystem", testee.getAddressIDByExternalSystem(), 
			is(businessPartnerAddress.getAddressIDByExternalSystem()));
	}
	
	@Test
	public void testToBusinessPartnerAddress() {
		// Given a business partner address...
		final BusinessPartnerAddress businessPartnerAddress = EntitySupplier.getDefaultAddress();
		
		// ... and address DTO constructed from the address
		final AddressDTO testee = AddressDTO.of(businessPartnerAddress);
		
		// When the address DTO is converted back to BusinessPartnerAddress
		final BusinessPartnerAddress businessPartnerAddressFromDTO = MapperUtils.map(
				testee, BusinessPartnerAddress.builder().build());
		
		// Then it is the same
		assertThat("An address converted to AddressDTO and back should be equal to the initial one", 
				businessPartnerAddressFromDTO, is(equalTo(businessPartnerAddress)));
	}
	
	/**
	 * Helper method to generate the
	 * {@link AddressDTOTest#testAddressDTOSetsAllFieldsFromBusinessPartnerAddress()}
	 * method
	 */
	@Test
	@Ignore
	public void generateTest() {
		final Field[] fields = AddressDTO.class.getDeclaredFields();
		
		final String testMethodBody = Arrays.asList(fields).stream()
			.filter(field -> !Modifier.isStatic(field.getModifiers())) // filter out static fields 
			.map(Field::getName) // get field names
			.map(name -> String.format("\tassertThat(\"%s\", testee.get%s(), \n\t\tis(businessPartnerAddress.get%<s()));", 
					name, 
					upperCaseFirstChar(name)))
			.collect(Collectors.joining("\n"));
		
		System.out.println("@Test");
		System.out.println(String.format("\tpublic void test%sSetsAllFieldsFrom%s() {", 
				AddressDTO.class.getSimpleName(),
				BusinessPartnerAddress.class.getSimpleName()));
		System.out.println(String.format("\tfinal %s businessPartnerAddress = EntitySupplier.getDefaultAddress();\n", 
				BusinessPartnerAddress.class.getSimpleName()));
		System.out.println(String.format("\tfinal %s testee = %<s.of(businessPartnerAddress);\n", 
				AddressDTO.class.getSimpleName()));
		System.out.println(testMethodBody);
		System.out.println("}");
	}
	
	private String upperCaseFirstChar(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
