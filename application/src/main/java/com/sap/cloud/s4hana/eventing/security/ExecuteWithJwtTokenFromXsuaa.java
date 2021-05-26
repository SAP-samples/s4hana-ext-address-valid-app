package com.sap.cloud.s4hana.eventing.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPException;
import com.sap.cloud.sdk.cloudplatform.security.JwtBasedRequestContextExecutor;

/**
 * Spring interceptor AOP annotation.
 * <p>
 * Annotated methods are executed with a JWT token from the XSUAA service. This
 * enables SAP S/4HANA Cloud SDK to fetch a destination without the logged in
 * user request context, when a method is executed e.g.:
 * <ul>
 * <li>during application startup</li>
 * <li>via an event from SAP Business Technology Platform (BTP) Enterprise Messaging Service</li>
 * <li>by another service (e.g. Scheduler service)</li>
 * <li>by a user which is not logged in to XSUAA, e.g. when a user executes a
 * request to an unprotected REST endpoint.</li>
 * </ul>
 * 
 * @see ExecuteWithJwtTokenFromXsuaaAspect#executeWithJwtTokenFromXsuaa(org.aspectj.lang.ProceedingJoinPoint)
 * @see JwtBasedRequestContextExecutor#withXsuaaServiceJwt()
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecuteWithJwtTokenFromXsuaa {
	
	/**
	 * @return error message to be logged by
	 *         {@link ExecuteWithJwtTokenFromXsuaaAspect} in case a throwable is
	 *         thrown during the intercepted method invocation and also to be
	 *         used as the message for an {@link SAPException} in which the
	 *         throwable will be wrapped.
	 */
	String error() default "Error when trying to execute a method using a JWT token from XSUAA service";
	
}
