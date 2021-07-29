package org.mifosplatform.organisation.address.command;

import java.util.Set;


public class AddressCommand {
	
	private final Long clientId;
	private final String addressKey;
	private final String addressNo;
	private final String street;
	private final String city;
	private final String state;
	private final String country;
	private final String zip;
	private final Set<String> modifiedParameters;
	

	public AddressCommand(final Set<String> modifiedParameters, final Long clientid, final String addressKey,final String addressNo,
			final String street, final String zip, final String city, final String state,final String country) {
		
		this.modifiedParameters=modifiedParameters;		
		this.clientId=clientid;
		this.addressKey=addressKey;
		this.addressNo=addressNo;
		this.state=state;
		this.street=street;
		this.city=city;
		this.country=country;
		this.zip=zip;
		
	}



	public Long getClientId() {
		return clientId;
	}



	public String getAddressKey() {
		return addressKey;
	}



	public String getAddressNo() {
		return addressNo;
	}



	public String getStreet() {
		return street;
	}



	public String getCity() {
		return city;
	}



	public String getState() {
		return state;
	}



	public String getCountry() {
		return country;
	}
	


	public String getZip() {
		return zip;
	}
	public boolean isAddressKeyChanged() {
		return this.modifiedParameters.contains("addressKey");
	}

	public boolean isAddressNOChanged() {
		return this.modifiedParameters.contains("addressNo");
	}

	public boolean isStreetChanged() {
		return this.modifiedParameters.contains("street");
	}
	
	public boolean isCityChanged() {
		return this.modifiedParameters.contains("city");
	}

	public boolean isStateChanged() {
		return this.modifiedParameters.contains("state");
	}

	public boolean isCountryChanged() {
		return this.modifiedParameters.contains("country");
	}
	public boolean isZipChanged() {
		return this.modifiedParameters.contains("zip");
	}

	

}
