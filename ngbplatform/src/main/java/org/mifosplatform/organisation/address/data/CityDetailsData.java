package org.mifosplatform.organisation.address.data;

public class CityDetailsData {

	private String cityName;
	private String cityCode;
	private String state;
	private String country;
	private String district;

	public CityDetailsData(final String cityName, final String cityCode) {

		this.cityCode = cityCode;
		this.cityName = cityName;
	}

	public CityDetailsData(final String cityName, final String cityCode,
			final String state, final String country, final String district) {

		this.cityCode = cityCode;
		this.cityName = cityName;
		this.state = state;
		this.country = country;
		this.district = district;
		
	}

	public String getCityName() {
		return cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}
	
	public String getDistrict() {
		return district;
	}

}
