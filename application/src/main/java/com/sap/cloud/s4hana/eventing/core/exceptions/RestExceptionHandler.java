package com.sap.cloud.s4hana.eventing.core.exceptions;

import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
 
	@ExceptionHandler
	protected ResponseEntity<DetailedErrorResponse> handleODataException(Throwable exception,
			WebRequest request) throws JsonProcessingException {
		
		return ResponseEntity.status(getHttpStatus(exception)).body(createErrorResponse(exception));
	}

	private DetailedErrorResponse createErrorResponse(Throwable exception) {
        final DetailedErrorResponse errorResponse = DetailedErrorResponse.of(/* code =*/ exception.getClass().getSimpleName(), 
        		exception.getMessage());
        
        // Optional: fill inner errors recursively
     	if (exception.getCause() != null) {
     		errorResponse.setInnerError(createErrorResponse(exception.getCause()));
     	}
        
        return errorResponse;
	}
    
    private HttpStatus getHttpStatus(Throwable exception) {
        final ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        
        if (responseStatusAnnotation == null) {
        	return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
		return responseStatusAnnotation.code();
    }
}
