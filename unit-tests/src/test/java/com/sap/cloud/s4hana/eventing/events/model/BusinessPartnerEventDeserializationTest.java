package com.sap.cloud.s4hana.eventing.events.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.sap.cloud.s4hana.eventing.testutil.JsonDeserializationTester;

@RunWith(Theories.class)
public class BusinessPartnerEventDeserializationTest extends JsonDeserializationTester<BusinessPartnerEvent, BusinessEvent<?>> {
	
	/**
	 * Context should be properly deserialized for both event types using Jackson deserialization
	 * 
	 * @see 
	 */
	@DataPoints
	public static final String[] JSON_TYPE_IDS = {BusinessPartnerEvent.JSON_TYPE_ID_CHANGED, BusinessPartnerEvent.JSON_TYPE_ID_CREATED};
	
	@Override
	public Class<? super BusinessEvent<?>> getBaseClass() {
		return BusinessEvent.class;
	}

	@Override
	public Class<BusinessPartnerEvent> getConcreteClass() {
		return BusinessPartnerEvent.class;
	}

	@Override
	public String getTesteeJson(String eventType) {
		return "{\r\n" + 
				"  \"eventType\": \"" + eventType + "\",\r\n" + 
				"  \"cloudEventsVersion\": \"0.1\",\r\n" + 
				"  \"source\": \"\",\r\n" + 
				"  \"eventID\": \"ABY+LHs5Hti56Xn6eGqGVw==\",\r\n" + 
				"  \"eventTime\": \"2018-11-13T13:55:44Z\",\r\n" + 
				"  \"schemaURL\": \"/sap/opu/odata/IWXBE/BROWSER_SRV/\",\r\n" + 
				"  \"contentType\": \"application/json\",\r\n" + 
				"  \"data\": {\r\n" + 
				"    \"KEY\": [\r\n" + 
				"      {\r\n" + 
				"        \"BUSINESSPARTNER\": \"9980021470\"\r\n" + 
				"      }\r\n" + 
				"    ]\r\n" + 				 
				"  }\r\n" + 
				"}";
	}

	@Override
	public void assertDeserializedTestee(final BusinessPartnerEvent testee) {
		assertThat("String contentType", testee.getContentType(), is(equalTo("application/json")));
		
		assertThat("Business partner key from BUSINESSPARTNER", testee.getPayload().getBusinessPartnerKey(), is(equalTo("9980021470")));
	}

}
