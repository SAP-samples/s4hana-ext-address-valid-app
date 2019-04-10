package com.sap.cloud.s4hana.eventing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@EnableAspectJAutoProxy
@SpringBootApplication
@ComponentScan({"com.sap.cloud.sdk.s4hana.datamodel.odata.services", "com.sap.cloud.s4hana.eventing"})
@ServletComponentScan({"com.sap.cloud.sdk.s4hana.datamodel.odata.services", "com.sap.cloud.s4hana.eventing"})
public class Application extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
    
}
