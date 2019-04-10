package com.sap.cloud.s4hana.eventing.countries.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.country.CountryText;

public class CountryServiceTest {
	
	@ClassRule
    public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
	
	@Rule
	public WireMockRule wireMockRule = environmentMock.mockS4Hana();
	
	CountryService testee = new CountryService(environmentMock.getErpConfigContext(), null, null);
	
	@Test
	public void testGetAll() {
		// Given a WireMock fixture of SAP S/4HANA Cloud respoonse defined in
		// "src/test/resources/mappings/YY1_COUNTRIES_CDS/Germany.json" file
		
		// When
		final List<CountryText> countries = testee.getAll();
		
		// Then
		assertThat("Germany is in the list and has DE as its ISO code", 
				countries, 
				hasItem(allOf(
						hasProperty("countryName", equalTo("Germany")),
						hasProperty("country", equalTo("DE")))));
	}

}
