package com.sap.cloud.s4hana.eventing.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Security exception")
public class SAPSecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public SAPSecurityException(String message) {
        super(message);
    }
    
    public SAPSecurityException(Throwable throwable) {
        super(throwable);
    }
    
    public SAPSecurityException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
