package org.mifosplatform.organisation.address.data;

public class CountryDetails {

	private final Long id;
	private final String countryName;
	
	public CountryDetails(final Long id, final String countryName) {
           
		this.id=id;
		this.countryName=countryName;
	
	
	}

	public Long getId() {
		return id;
	}

	public String getCountryName() {
		return countryName;
	}
	
	

}
