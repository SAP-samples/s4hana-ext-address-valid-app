package com.sap.cloud.s4hana.eventing.events.model;

import java.util.Collection;
import java.util.Collections;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessPartnerEvent extends BusinessEvent<BusinessPartnerEvent.Payload> {
	
	public static final String JSON_TYPE_ID_CREATED = "BO.BusinessPartner.Created";
	public static final String JSON_TYPE_ID_CHANGED = "BO.BusinessPartner.Changed";
	
	/**
	 * Example JSON object:
	 * 
	 * {"KEY":[{"BUSINESSPARTNER":"9980021470"}]}}
	 *
	 */
	public static class Payload {
		
		public static class BusinessPartnerKey {
			@NotNull
			@JsonProperty("BUSINESSPARTNER")
			protected String businessPartnerKey;
			
			@Override
			public String toString() {
				return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
			}
			
		}
		
		@Valid
		@NotNull
		@Size(min = 1, max = 1)
		@JsonProperty("KEY")
		protected Collection<@NotNull BusinessPartnerKey> keys;
		
		public String getBusinessPartnerKey() {
			return keys.iterator().next().businessPartnerKey;
		}
		
		public void setBusinessPartnerKey(String businessPartnerKey) {
			final BusinessPartnerKey key = new BusinessPartnerKey();
			key.businessPartnerKey = businessPartnerKey;
			keys = Collections.singletonList(key);
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
	}

	@NotNull
	@Valid
	@Override
	public Payload getPayload() {
		return super.getPayload();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
