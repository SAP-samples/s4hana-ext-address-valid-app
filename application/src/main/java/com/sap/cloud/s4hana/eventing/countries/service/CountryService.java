package com.sap.cloud.s4hana.eventing.countries.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.country.CountryText;

@Service
public class CountryService {

	private static final Logger log = LoggerFactory.getLogger(CountryService.class);
	
	public static final String DEFAULT_SERVICE_PATH = "/sap/opu/odata/sap/YY1_COUNTRIES_CDS";
	public static final String DEFAULT_ENTITY_SET_NAME = "YY1_COUNTRIES";
	
	private final String odataServicePath;
	private final String odataEntitySetName;
	
	private final ErpConfigContext erpConfigContext;
	
	/**
	 * @param odataServicePath
	 *            Service path of the OData service of a custom CDS view with
	 *            countries value help. If the provided value is blank (null or
	 *            empty even when trimmed), then
	 *            {@link CountryService#DEFAULT_SERVICE_PATH} is used as a
	 *            default value.
	 * @param odataEntitySetName
	 *            EDM Entity name of the OData service of a custom CDS view with
	 *            countries value help. If the provided value is blank (null or
	 *            empty even when trimmed), then
	 *            {@link CountryService#DEFAULT_ENTITY_SET_NAME} is used as a
	 *            default value.
	 */
	@Autowired
	public CountryService(ErpConfigContext erpConfigContext,			
			@Value("${s4hana.countryService.odataServicePath:}") String odataServicePath, 
			@Value("${s4hana.countryService.odataEntitySetName:}") String odataEntitySetName) {
		
		if (StringUtils.isBlank(odataServicePath)) {
			odataServicePath = DEFAULT_SERVICE_PATH;
		}
		
		if (StringUtils.isBlank(odataEntitySetName)) {
			odataEntitySetName = DEFAULT_ENTITY_SET_NAME;
		}
		
		this.erpConfigContext = erpConfigContext;
		this.odataServicePath = odataServicePath;
		this.odataEntitySetName = odataEntitySetName;
	}

	/**
	 * @return the list of countries from S/4HANA via a Custom CDS View.
	 *         <p>
	 *         We reuse the existing {@link CountryText} Virtual Data Model
	 *         Entity from S/4HANA Cloud SDK here for the sake of simplicity.
	 *         <p>
	 *         In case there is no existing {@link VdmEntity} that can be reused
	 *         to query the Custom CDS View, consider using Virtual Data Model
	 *         Generator of SAP S/4HANA Cloud SDK.
	 * 
	 * @see <a href=
	 *      "https://blogs.sap.com/2018/03/29/sap-s4hana-cloud-sdk-version-1.9.4-is-available/#vdmGeneratorPreview">Announcement
	 *      of the preview release of Virtual Data Model (VDM) generator</a>
	 *
	 */
	public List<CountryText> getAll() throws SAPODataException {
		try {
			return ODataQueryBuilder.withEntity(odataServicePath, odataEntitySetName)
				.build()
				.execute(erpConfigContext)
				.asList(CountryText.class);
		} catch (Exception e) {
			final String message = "Error when getting the list of countries";
			log.error(message, e);
			throw new SAPODataException(message, e);
		}
	}

}
