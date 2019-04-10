package com.sap.cloud.s4hana.eventing.core.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;

/**
 * Convenience methods for {@link ModelMapper} 
 *
 */
public class MapperUtils {
	
	private MapperUtils() {
		// prevents util class from being instantiated 
	}

	/**
	 * @return preconfigured {@link ModelMapper}
	 */
	public static ModelMapper getMapper() {
		final ModelMapper mapper = new ModelMapper();
		
		mapper.getConfiguration()
			// produce idempotent results
			.setMatchingStrategy(MatchingStrategies.STRICT)
			// read fields directly to avoid calling get...orFetch methods
			.setFieldMatchingEnabled(true)
			.setFieldAccessLevel(AccessLevel.PRIVATE)
			// don't use getters and setters
			.setDestinationNamingConvention(NamingConventions.JAVABEANS_ACCESSOR)
			.setSourceNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
		
		return mapper;
	}
	
	/**
	 * Maps {@code source} object to an object of {@code destinationType} using
	 * a preconfigured {@link ModelMapper}
	 * 
	 * @param <T>
	 *            type of {@code source} object
	 * @param <D>
	 *            type of resut object
	 * 
	 * @see ModelMapper#map(Object, Class)
	 */
	public static <T, D> D map(T source, Class<D> destinationType) {
		return getMapper().map(source, destinationType);
	}
	
	/**
	 * Maps {@code source} object to an existing {@code destination} object
	 * using a preconfigured {@link ModelMapper}
	 * 
	 * @param <T>
	 *            type of {@code source} object
	 * @param <D>
	 *            type of resut object
	 * @returns updated {@code destination} object
	 * @see ModelMapper#map(Object, Class)
	 */
	public static <T, D> D map(T source, D destination) {
		new ModelMapper().map(source, destination);
		return destination;
	}
	
}