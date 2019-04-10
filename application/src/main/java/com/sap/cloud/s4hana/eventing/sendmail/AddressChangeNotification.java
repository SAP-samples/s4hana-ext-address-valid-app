package com.sap.cloud.s4hana.eventing.sendmail;

import com.sap.cloud.s4hana.eventing.businesspartner.model.AddressDTO;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;

/**
 * Freemarker data model for address change event notification.
 *
 * @see <a href="https://freemarker.apache.org/docs/pgui_datamodel.html">Apache
 *      Freemarker Documentation - The Data Model</a>
 */
public class AddressChangeNotification {

	private final BusinessPartner businessPartner;
	private final AddressDTO address;
	private final BusinessPartner contact;
	private final String emailAddress;
	private final String confirmationLink;

	public static AddressChangeNotification of(BusinessPartner businessPartner, 
			AddressDTO address,
			BusinessPartner contact,
			String emailAddress,
			String confirmationLink) {
		
		return new AddressChangeNotification(businessPartner, 
				address, contact, emailAddress, confirmationLink);
	}

	protected AddressChangeNotification(BusinessPartner businessPartner, 
			AddressDTO address,
			BusinessPartner contact,
			String emailAddress,
			String confirmationLink) {
		
		super();
		this.businessPartner = businessPartner;
		this.address = address;
		this.contact = contact;
		this.emailAddress = emailAddress;
		this.confirmationLink = confirmationLink;
	}

	public BusinessPartner getBusinessPartner() {
		return businessPartner;
	}

	public AddressDTO getAddress() {
		return address;
	}

	public BusinessPartner getContact() {
		return contact;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getConfirmationLink() {
		return confirmationLink;
	}
	
}
