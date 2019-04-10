package com.sap.cloud.s4hana.eventing.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPException;
import com.sap.cloud.sdk.cloudplatform.security.JwtBasedRequestContextExecutor;
import com.sap.cloud.sdk.cloudplatform.servlet.RequestContextAccessor;

@Aspect
@Component
@Profile("cloud")
public class ExecuteWithJwtTokenFromXsuaaAspect {
	
	private static final Logger log = LoggerFactory.getLogger(ExecuteWithJwtTokenFromXsuaaAspect.class);
	
	/**
	 * Aspect-oriented Programming (AOP) advice that executes the {@code method}
	 * annotated with @{@link ExecuteWithJwtTokenFromXsuaa} annotation with a
	 * JWT token from XSUAA service.
	 * 
	 * @param method
	 *            method to be executed
	 * @return the value returned by the executed {@code method}
	 * @see JwtBasedRequestContextExecutor#withXsuaaServiceJwt()
	 */
	@Around("@annotation(ExecuteWithJwtTokenFromXsuaa)")
	public Object executeWithJwtTokenFromXsuaa(ProceedingJoinPoint method) throws Exception {
		JwtBasedRequestContextExecutor executor = new JwtBasedRequestContextExecutor();

		// for user requests, parent request context should be used
		if (RequestContextAccessor.getCurrentRequestContext().isPresent()) {
			executor = executor.withParentRequestContext();
		}
		
		return executor.
				withXsuaaServiceJwt().
				execute(() -> proceed(method));
	}
	
	/**
	 * Executes the {@code method} and returns the logs throwed
	 * {@link Throwable} and wraps it
	 * 
	 * @param method
	 *            method to be executed
	 * @return the value returned by the executed {@code method}
	 * @throws SAPException
	 *             in case any {@link Throwable} is thrown during the execution
	 *             of {@code method}. This is needed because
	 *             {@link JwtBasedRequestContextExecutor#execute(java.util.concurrent.Callable)}
	 *             method signature declares {@link Exception} to be thrown
	 *             while {@link ProceedingJoinPoint#proceed()} can throw
	 *             {@link Throwable} superclass.
	 */
	protected static Object proceed(ProceedingJoinPoint method) throws SAPException {
		final MethodSignature methodSignature = (MethodSignature) method.getSignature();
		log.debug("{} method is executed with a JWT token from XSUAA service", methodSignature);
		
		try {
			return method.proceed();
		} catch (Throwable t) {
			// get error message from the aspect annotation
			final ExecuteWithJwtTokenFromXsuaa aspectAnnotation = methodSignature.getMethod().getAnnotation(ExecuteWithJwtTokenFromXsuaa.class);
			final String message = aspectAnnotation.error();
			
			log.warn(message, t);
			
	        throw new SAPException(message, t);
		}
	}

}
