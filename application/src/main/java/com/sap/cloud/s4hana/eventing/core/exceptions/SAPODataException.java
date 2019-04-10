package com.sap.cloud.s4hana.eventing.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "OData Exception")
public class SAPODataException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SAPODataException() {
	}
	
    public SAPODataException(String message) {
        super(message);
    }
    
    public SAPODataException(Throwable throwable){
        super(throwable);
    }
    
    public SAPODataException(String message, Throwable throwable){
        super(message,throwable);
    }
}