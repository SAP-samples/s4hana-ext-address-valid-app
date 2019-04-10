package com.sap.cloud.s4hana.eventing.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Mailing exception")
public class SAPMailingException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public SAPMailingException(String message) {
        super(message);
    }
    
    public SAPMailingException(Throwable throwable) {
        super(throwable);
    }
    
    public SAPMailingException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
