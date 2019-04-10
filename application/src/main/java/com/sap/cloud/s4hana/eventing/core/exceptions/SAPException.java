package com.sap.cloud.s4hana.eventing.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SAPException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SAPException() {
	}
	
    public SAPException(String message) {
        super(message);
    }
    
    public SAPException(Throwable throwable){
        super(throwable);
    }
    
    public SAPException(String message, Throwable throwable){
        super(message,throwable);
    }
}