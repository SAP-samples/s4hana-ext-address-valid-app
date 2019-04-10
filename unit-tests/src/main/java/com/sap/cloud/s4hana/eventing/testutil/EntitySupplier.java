package com.sap.cloud.s4hana.eventing.testutil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

import com.sap.cloud.s4hana.eventing.businesspartner.model.AddrConfState;
import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.s4hana.eventing.businesspartner.model.CustomBusinessPartner;
import com.sap.cloud.s4hana.eventing.businesspartner.service.CustomBusinessPartnerService;
import com.sap.cloud.s4hana.eventing.sendmail.AddressChangeNotification;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BPContactToFuncAndDept;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;

/**
 * This class supplies dummy VDM entities to be used in tests.
 */
public class EntitySupplier {
	
	/**
	 * Default test value for
	 * {@link CustomBusinessPartner#ADDRESS_CONFIRMATION_STATE_CUSTOM_FIELD_NAME} 
	 * custom field
	 * 
	 * @see CustomBusinessPartner#getAddressConfirmationState()
	 * @see CustomBusinessPartner#setAddressConfirmationState(AddrConfState)
	 * @see CustomBusinessPartner#ADDRESS_CONFIRMATION_STATE
	 */
	public static final AddrConfState ADDRESS_CONFIRMATION_STATE = AddrConfState.INITIAL;
	
	/**
	 * Default test value for
	 * {@link CustomBusinessPartner#ADDRESS_CHECKSUM_CUSTOM_FIELD_NAME} custom
	 * field
	 * 
	 * @see CustomBusinessPartner#getAddressChecksum()
	 * @see CustomBusinessPartner#setAddressChecksum(String)
	 * @see CustomBusinessPartner#ADDRESS_CHECKSUM
	 */
	public static final String ADDRESS_CHECKSUM = "Address checksum";
	
	/**
	 * Key of a {@link BusinessPartner} that exists in the test S/4HANA system.
	 * <p>
	 * A part of the key of {@link BusinessPartnerAddress}. Another part is
	 * {@link BusinessPartnerAddress#ADDRESS_ID}
	 * 
	 * @see BusinessPartner#getBusinessPartner()
	 * @see BusinessPartner#setBusinessPartner(String)
	 * @see BusinessPartner#BUSINESS_PARTNER
	 * 
	 * @see BusinessPartnerAddress#getBusinessPartner()
	 * @see BusinessPartnerAddress#setBusinessPartner(String)
	 * @see BusinessPartnerAddress#BUSINESS_PARTNER
	 */
	public final static String EXISTING_BUSINESS_PARTNER_KEY = "1000090";
	
	/**
	 * Key of a {@link BusinessPartner} that does NOT exist in the test S/4HANA
	 * system.
	 * <p>
	 * A part of the key of {@link BusinessPartnerAddress}. Another part is
	 * {@link BusinessPartnerAddress#ADDRESS_ID}
	 * 
	 * @see BusinessPartner#getBusinessPartner()
	 * @see BusinessPartner#setBusinessPartner(String)
	 * @see BusinessPartner#BUSINESS_PARTNER
	 * 
	 * @see BusinessPartnerAddress#getBusinessPartner()
	 * @see BusinessPartnerAddress#setBusinessPartner(String)
	 * @see BusinessPartnerAddress#BUSINESS_PARTNER
	 */
	public final static String NONEXISTENT_BUSINESS_PARTNER_KEY = "notexistkey";
	
	/**
	 * ID of a {@link BusinessPartnerAddress} of the existing
	 * {@link BusinessPartner} with key
	 * {@link EntitySupplier#EXISTING_BUSINESS_PARTNER_KEY}
	 * <p>
	 * A part of the key of {@link BusinessPartnerAddress}. Another part is
	 * {@link BusinessPartnerAddress#BUSINESS_PARTNER}
	 * 
	 * @see BusinessPartnerAddress#getAddressID()
	 * @see BusinessPartnerAddress#setAddressID(String)
	 * @see BusinessPartnerAddress#ADDRESS_ID
	 */
	public static final String EXISTING_ADDRESS_ID = "37384";
	
	public static CustomBusinessPartner getDefaultBusinessPartner() {
		final CustomBusinessPartner result = CustomBusinessPartner.of(BusinessPartner.builder()
				.businessPartner("0000000000")
				.businessPartnerFullName("A Cömpany That Makes Everything Inc.")
				.customer("0000000000")
				.businessPartnerAddress(getDefaultAddress())
				.build());
		
		result.setAddressConfirmationState(ADDRESS_CONFIRMATION_STATE);
		result.setAddressChecksum(ADDRESS_CHECKSUM);
		
		return result;
	}
	
	public static CustomBusinessPartner getNewlyCreatedBusinessPartnerRoot() {
		final CustomBusinessPartner result = CustomBusinessPartner.of(BusinessPartner.builder()
                .businessPartner("0000000000")
                .businessPartnerFullName("A Cömpany That Makes Everything Inc.")
                .customer("0000000000")
                .build());
        
		result.setAddressConfirmationState(null);
        result.setAddressChecksum(null);
        
        return result;
	}
	
	public static CustomBusinessPartner getDefaultBusinessPartnerRoot() {
		final CustomBusinessPartner result = CustomBusinessPartner.of(BusinessPartner.builder()
                .businessPartner("0000000000")
                .businessPartnerFullName("A Cömpany That Makes Everything Inc.")
                .customer("0000000000")
                .build());
        
		result.setAddressConfirmationState(ADDRESS_CONFIRMATION_STATE);
		result.setAddressChecksum(ADDRESS_CHECKSUM);
		        
		return CustomBusinessPartner.of(result);
    }
   
    public static CustomBusinessPartner getNoCustomerBusinessPartner() {
    	final CustomBusinessPartner result = CustomBusinessPartner.of(BusinessPartner.builder()
               .businessPartner("0000000000")
               .businessPartnerFullName("A Cömpany That Makes Everything Inc.")
               .businessPartnerAddress(getDefaultAddress())
               .build());
       
    	result.setAddressConfirmationState(ADDRESS_CONFIRMATION_STATE);
		result.setAddressChecksum(ADDRESS_CHECKSUM);
       
        return CustomBusinessPartner.of(result);
   }
    
    public static CustomBusinessPartner getCustomerAndPersonBusinessPartner() {
    	final CustomBusinessPartner result = CustomBusinessPartner.of(BusinessPartner.builder()
               .businessPartner("0000000000")
               .businessPartnerFullName("A Persön That Makes Everything")
               .firstName("Elon")
               .lastName("Musk")
               .customer("0000000000")
               .isNaturalPerson("X")
               .businessPartnerAddress(getDefaultAddress())
               .build());
       
    	result.setAddressConfirmationState(ADDRESS_CONFIRMATION_STATE);
		result.setAddressChecksum(ADDRESS_CHECKSUM);
       
        return CustomBusinessPartner.of(result);
   }
	
	public static CustomBusinessPartner getDefaultContactBusinessPartner() {
		return CustomBusinessPartner.of(BusinessPartner.builder()
				.businessPartner("9876543210")
				.businessPartnerFullName("A Cömpany That Makes Everything Inc. contact person")
				.academicTitle("Prof. Dr.-Ing. habil.").firstName("Jöhny").lastName("Schurnikidze-Popugaichikov Jr.")
				.personFullName("Prof. Dr.-Ing. habil. Jöhnathan Schurnikidze-Popugaichikov Jr.")
				.build());
	}
	
	public static BusinessPartnerAddress getDefaultAddress() {
		return BusinessPartnerAddress.builder()
				// manually maintained fields to check if the 
				.addressID("0123456789")
				.businessPartner("9876543210")
				.person("9876543210") // MaxLength: 10
				.streetName("Very long street name "
						+ "who knows how long could they be")
				.houseNumber("7 corp. 8A") // MaxLength: 10
				.houseNumberSupplementText("10ChrsOnly") // MaxLength: 10 
				.cityCode("1234567-CBLR")
				.cityName("Chateaubelair")
				.county("Chateaubelair County")
				.country("DE") // MaxLength: 3
				.language("DE") // MaxLength: 2
				// generated code - see generateGetDefaultAddress() method
				.authorizationGroup("1234") // MaxLength: 4
				.additionalStreetPrefixName("additionalStreetPrefixName")
				.additionalStreetSuffixName("additionalStreetSuffixName")
				.addressTimeZone("UTC+06") // MaxLength: 6
				.careOfName("careOfName")
				.companyPostalCode("0123456789") // MaxLength: 10
				.deliveryServiceNumber("0123456789") // MaxLength: 10
				.deliveryServiceTypeCode("1234") // MaxLength: 4
				.district("district")
				.formOfAddress("1234") // MaxLength: 4
				.fullName("fullName")
				.homeCityName("homeCityName")
				.pOBox("pOBox")
				.pOBoxDeviatingCityName("pOBoxDeviatingCityName")
				.pOBoxDeviatingCountry("123") // MaxLength: 3
				.pOBoxDeviatingRegion("123") // MaxLength: 3
				.pOBoxIsWithoutNumber(true)
				.pOBoxLobbyName("pOBoxLobbyName")
				.pOBoxPostalCode("postalCode") // MaxLength: 10
				.postalCode("postalCode")
				.prfrdCommMediumType("pmt") // MaxLength: 3
				.region("rgn") // MaxLength: 3
				.streetPrefixName("streetPrefixName")
				.streetSuffixName("streetSuffixName")
				.taxJurisdiction("taxJurisdiction")
				.transportZone("trnsptZone") // MaxLength: 10
				.addressIDByExternalSystem("addressIDByExtSys") // MaxLength: 20
				.build();
	}
	
	public static List<BusinessPartnerAddress> getDefaultListAddress() {
	    List<BusinessPartnerAddress>  list = new ArrayList<>();
	    list.add(getDefaultAddress());
	   return list;
	}
	
	public static BPContactToFuncAndDept getDefaultContact() {
		return BPContactToFuncAndDept.builder()
				.businessPartnerCompany("9876543210")
				.businessPartnerPerson("0000000001")
				.emailAddress("contact@example.com")
				.build();
	}
	
	public static BPContactToFuncAndDept getQualityOfficerContact() {
        return BPContactToFuncAndDept.builder()
                .businessPartnerCompany("9876543210")
                .businessPartnerPerson("0000000001")
                .emailAddress("contact@example.com")
                .contactPersonFunction(CustomBusinessPartnerService.DEFAULT_CONTACT_PERSON_FUNCTION)
                .build();
	}
	
    public static BPContactToFuncAndDept getQualityAssuranceContact() {
        return BPContactToFuncAndDept.builder()
                .businessPartnerCompany("9876543210")
                .businessPartnerPerson("0000000001")
                .emailAddress("contact@example.com")
                .contactPersonDepartment(CustomBusinessPartnerService.DEFAULT_CONTACT_PERSON_DEPARTMENT)
                .build();
    }

    public static BPContactToFuncAndDept getQualityAssuranceQualityOfficeContact() {
       return BPContactToFuncAndDept.builder()
               .businessPartnerCompany("9876543210")
               .businessPartnerPerson("0000000001")
               .emailAddress("contact@example.com")
               .contactPersonFunction(CustomBusinessPartnerService.DEFAULT_CONTACT_PERSON_FUNCTION)
               .contactPersonDepartment(CustomBusinessPartnerService.DEFAULT_CONTACT_PERSON_DEPARTMENT)
               .build();
   } 
    
    public static BPContactToFuncAndDept getContactWithNoEmail() {
        return BPContactToFuncAndDept.builder()
                .businessPartnerCompany("9876543210")
                .businessPartnerPerson("0000000001")
                .build();
    }	
	
	public static AddressChangeNotification getDefaultAddressChangeNotification() {
		return AddressChangeNotification.of(
				getDefaultBusinessPartner(), 
				AddressDTO.of(getDefaultAddress()), 
				getDefaultContactBusinessPartner(),
				getDefaultContact().getEmailAddress(),
				"dummyUrlToken");
	}
	
	public static List<BPContactToFuncAndDept> getListOfContacts() {
	    List<BPContactToFuncAndDept> list = new ArrayList<>();
	    list.add(getDefaultContact());
	    list.add(getContactWithNoEmail());
	    return list;
	}
	
   public static List<BPContactToFuncAndDept> getListOfContactsWithAll() {
        List<BPContactToFuncAndDept> list = new ArrayList<>();
        list.add(getDefaultContact());
        list.add(getContactWithNoEmail());
        list.add(EntitySupplier.getQualityAssuranceContact());
        list.add(EntitySupplier.getQualityAssuranceQualityOfficeContact());
        list.add(EntitySupplier.getQualityOfficerContact());
        return list;
    }
	
	/*
	 * Helper method to generate the getDefaultAddress() method
	 */
	
	@Test
	public void generateGetDefaultAddress() {
		final Field[] fields = AddressDTO.class.getDeclaredFields();
		
		final List<String> manuallyFilledFields = Arrays.asList(
				"addressID", 
				"businessPartner", 
				"person", 
				"streetName", 
				"houseNumber", 
				"houseNumberSupplementText", 
				"cityCode", 
				"cityName", 
				"county", 
				"country",
				"language");
		
		final String dummyFieldsInitializer = Arrays.asList(fields).stream()
			.filter(field -> !Modifier.isStatic(field.getModifiers())) // filter out static fields
			.filter(field -> !manuallyFilledFields.contains(field.getName())) // filter out fields that we fill manually
			.map(field -> String.format("\t\t\t.%s(%s)", 
						field.getName(),
						getDefaultValue(field)))
			.collect(Collectors.joining("\n"));
		
		System.out.println(dummyFieldsInitializer);
	}

	/*
	 * The whole address doesn't exist in the S4 HANA Test System, but the IDs are from an existing address.
	 */
	public static BusinessPartnerAddress getExistingAddress() {
		final BusinessPartnerAddress result = getDefaultAddress();
		
		result.setAddressID(EXISTING_ADDRESS_ID);
		result.setBusinessPartner(EXISTING_BUSINESS_PARTNER_KEY);
		
		return result;
	}
	
	public static String getDefaultValue(Field field) {
		if (field.getType() == String.class) {
			return  "\"" + field.getName() + "\"";
		} else if (field.getType() == Boolean.class) {
			return  String.valueOf(true);
		} else if (field.getType() == UUID.class) {
			return  "UUID.fromString(\"7a427a00-33e0-4d36-b061-9a486e135f9c\")";
		} else if (field.getType() == Calendar.class) {
			return  "defaultCalendar";
		} else {
			throw new IllegalArgumentException("Field " + field + " type " + field.getType() + " is not supported!");
		}
	}

}
