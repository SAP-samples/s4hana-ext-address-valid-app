package com.sap.cloud.s4hana.eventing.events.model;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Event that comes from Enterprise Messaging Service as JSON.
 * <p>
 * Example JSON object:<br>
 * 
 * {<br>
 *	  "eventType": "BO.BusinessPartner.Created",<br>
 *	  "cloudEventsVersion": "0.1",<br>
 *	  "source": "https://...example.com:50000",<br>
 *	  "eventID": "ABY+LHs5Hti5z15rY1hHkw==",<br>
 *	  "eventTime": "2018-11-12T13:00:37",<br>
 *	  "schemaURL": "https://...example.com:50000/sap/opu/odata/IWXBE/BROWSER_SRV/",<br>
 *	  "contentType": "application/json",<br>
 *	  "data": {}<br>
 *	}
 *
 * @param <T>
 *            the type of event which determines the event payload stored in
 *            {@link BusinessEvent#getPayload()} retrieved from the "data" JSON
 *            property.
 *            
 * @see <a href=
 *      "https://github.com/FasterXML/jackson-docs/wiki/JacksonPolymorphicDeserialization">Polymorphic
 *      Type Handling on Jackson wiki</a>
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = BusinessEvent.JSON_TYPE_ID_SOURCE_PROPERTY)
@JsonSubTypes({
	@JsonSubTypes.Type(value = BusinessPartnerEvent.class, name = BusinessPartnerEvent.JSON_TYPE_ID_CREATED),
	@JsonSubTypes.Type(value = BusinessPartnerEvent.class, name = BusinessPartnerEvent.JSON_TYPE_ID_CHANGED)
})
public class BusinessEvent<T> {
	
	/**
	 * The name of @{@link JsonProperty} to be used as type id for
	 * {@link BusinessEventTypeIdResolver}.
	 * <p>
	 * The property value is NOT deserialized by JSON, this is why this class
	 * doesn't contain the event type property.
	 */
	public static final String JSON_TYPE_ID_SOURCE_PROPERTY = "eventType";
	
	/**
	 * HTTP Content-Type header for the event payload object stored in the
	 * "DATA" JSON property.
	 * <p>
	 * The value must be {@code application/json}.
	 */
	@Pattern(regexp = "application\\/json")
	@JsonProperty("contentType")
	private String contentType;
	
	@JsonProperty("eventID")
	private String eventID;
	
	@Valid
	@JsonProperty("data")
	private T payload;

	/* getters and setters */
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T data) {
		this.payload = data;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
