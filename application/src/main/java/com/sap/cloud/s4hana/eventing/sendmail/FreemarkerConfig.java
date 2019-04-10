package com.sap.cloud.s4hana.eventing.sendmail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPMailingException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Configures Apache Freemarker templating engine and provides its configuration
 * as singleton.
 */
@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {
	
	private static final Logger log = LoggerFactory.getLogger(FreemarkerConfig.class); 
	
	/**
	 * Path to templates folder relative to classpath.
	 * <p>
	 * Default value is {@code /templates/} which points to e.g.
	 * {@code /src/main/resources/templates/}
	 * 
	 * @see Configuration#setClassForTemplateLoading(Class, String)
	 */
	public static final String PATH_TO_TEMPLATES_FOLDER = "/templates/";

	/**
	 * @return {@link Configuration} singleton
	 * 
	 * @throws SAPMailingException
	 *             in case the configuration is not compatible with the current
	 *             Freemarker version
	 * 
	 * @see ClassTemplateLoader
	 * @see TemplateException
	 */
	@Bean 
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Configuration getConfiguration() throws SAPMailingException {
		
    	// Create your Configuration instance, and specify if up to what FreeMarker
    	// version (here 2.3.27) do you want to apply the fixes that are not 100%
    	// backward-compatible. See the Configuration JavaDoc for details
        Configuration cfg;
		try {
			cfg = new Configuration(Configuration.VERSION_2_3_27);
		} catch (IllegalArgumentException e) {
			final String message = "Cannot configure Apache Freemarker template engine which is used to compose email messages. "
					+ "The currently used version " + Configuration.getVersion() + " is not compatible with the configuration.";
			log.error(message);
			throw new SAPMailingException(message);
		}
        
	    // Specify the source where the template files come from
		cfg.setClassForTemplateLoading(this.getClass(), PATH_TO_TEMPLATES_FOLDER);
	
	    // Set the preferred charset template files are stored in. 
		// UTF-8 is a good choice in most applications
	    cfg.setDefaultEncoding("UTF-8");
	
	    // Sets how errors will appear.
	    // During web page *development* it is recommended to use
	    // TemplateExceptionHandler.HTML_DEBUG_HANDLER instead
	    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	
	    // Don't log exceptions inside FreeMarker that it will thrown at you anyway.
	    cfg.setLogTemplateExceptions(false);
	
	    // Wrap unchecked exceptions thrown during template processing into TemplateException-s.
	    cfg.setWrapUncheckedExceptions(true);
        
        return cfg;
    }
	
}
