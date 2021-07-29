package org.mifosplatform.organisation.address.data;



public class AddressLocationDetails {
	
  private final String countryCode;
  private final String countryName;
  private final String cityCode;
  private final String cityName;
  private final String stateCode;
  private final String stateName;
  private final String districtCode;
  private final String districtName;
  private final Long cityId;
  private final Long countryId;
  private final Long stateId;
  private final Long districtId;
  
  
  
	
	public AddressLocationDetails(final String countryCode,final String countryName,final String cityCode,final String cityName, 
				final String stateCode,final String stateName,final String districtCode,final String districtName,final Long countryId,final Long stateId,final Long cityId,
				final Long districtId){
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.cityCode = cityCode;
		this.cityName = cityName;
		this.stateCode = stateCode;
		this.stateName = stateName;
		this.districtCode = districtCode;
		this.districtName = districtName;
		this.cityId=cityId;
		this.stateId=stateId;
		this.countryId=countryId;
		this.districtId=districtId;
		
		
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public String getStateName() {
		return stateName;
	}

	public Long getCityId() {
		return cityId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public Long getDistrictId() {
		return districtId;
	}
	
	
}
