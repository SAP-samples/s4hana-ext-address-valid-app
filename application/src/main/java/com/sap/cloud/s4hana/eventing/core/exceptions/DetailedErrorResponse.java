package com.sap.cloud.s4hana.eventing.core.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic model to be used as a HTTP response body for REST API error responses
 *
 */
public class DetailedErrorResponse {
    
    @JsonProperty("status")
    private Integer httpStatusCode;
    
    public static class ErrorMessage {
    	
		@JsonProperty("value")
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("innererror")
	private DetailedErrorResponse innerError;
	
	private DetailedErrorResponse(String code, String message, DetailedErrorResponse innerError) {
		this.code = code;
		this.message = message;
		this.innerError = innerError;
	}
	
	public static DetailedErrorResponse of(String code, String message) {
		return DetailedErrorResponse.of(code, message, /* innerError = */ null);
	}
	
	public static DetailedErrorResponse of(String code, String message, DetailedErrorResponse innerError) {
		return new DetailedErrorResponse(code, message, innerError);
	}
	
	private DetailedErrorResponse() {
		// default constructor to enable deserialization by Jackson
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message; 
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DetailedErrorResponse getInnerError() {
		return innerError;
	}

	public void setInnerError(DetailedErrorResponse innerError) {
		this.innerError = innerError;
	}
	
}
