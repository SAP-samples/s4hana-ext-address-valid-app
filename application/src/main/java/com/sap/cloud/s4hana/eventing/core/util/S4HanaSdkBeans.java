package com.sap.cloud.s4hana.eventing.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.cloud.s4hana.eventing.security.ExecuteWithJwtTokenFromXsuaa;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.ErpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

@Configuration
public class S4HanaSdkBeans {

    @Bean
    BusinessPartnerService getBusinessPartnerService() {
        return new DefaultBusinessPartnerService();
    }
    
    /**
	 * Please consider setting {@code sap-language} and {@code sap-client}
	 * properties on your destination to avoid getting unnecessary log messages
	 * from SAP S/4HANA Cloud SDK.
	 * 
	 * @param erpDestinationName
	 *            The name of SAP S/4HANA Cloud destination. If the provided
	 *            value is blank (null or empty even when trimmed), then
	 *            {@link ErpDestination#getDefaultName()} is used as a default
	 *            value.
	 * 
	 * @see {@link ErpConfigContext#ErpConfigContext(String)}
	 */
    @Bean
    @ExecuteWithJwtTokenFromXsuaa
    ErpConfigContext getErpConfigContext(@Value("${s4hana.destination:}") String erpDestinationName) {
    	if (StringUtils.isBlank(erpDestinationName)) {
    		erpDestinationName = ErpDestination.getDefaultName();
    	}
    	
    	return new ErpConfigContext(erpDestinationName);
    }
    
}
