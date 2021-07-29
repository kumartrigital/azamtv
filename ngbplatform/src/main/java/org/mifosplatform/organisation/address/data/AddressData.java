package org.mifosplatform.organisation.address.data;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class AddressData {
	
	private Long id;
	private Long clientId;
	private String addressKey;
	private String addressNo;
	private String street;
	private String zip;
	private String city;
	private String state;
	private String country;
	private List<AddressData> datas;
	private List<String> countryData,stateData,cityData;
	private String data;
	private List<EnumOptionData> addressOptionsData; 
	private List<AddressData> addressData;
	private String addressType;
	private Long addressTypeId;
	private String district;
	private List<String> districtData;
	public  List<String> citiesData;
	
	

	public AddressData(final Long addressId, final Long clientId, final String addressKeyId,
			final String addressNo, final String street, final String zip, final String city,
			final String state, final String country,final String district, final String addressKey,final Long addressTypeId) {
     
		this.id=addressId;
		this.addressKey=addressKeyId;
		this.addressTypeId=addressTypeId;
		this.clientId=clientId;
		this.addressNo=addressNo;
		this.street=street;
		this.zip=zip;
		this.city=city;
		this.state=state;
		this.country=country;
		this.district=district;
		this.addressType=addressKey;
		
	
	
	}



	public AddressData(final List<AddressData> addressdata, final List<String> countryData, final List<String> statesData,
						final List<String> citiesData, final List<EnumOptionData> enumOptionDatas,final List<String> districtData) {
		
	this.datas=addressdata;
	this.countryData=countryData;
	this.stateData=statesData;
	this.cityData=citiesData;
	this.addressOptionsData=enumOptionDatas;
	this.districtData=districtData;
	
	}



	public AddressData(final Long id, final String data) {

	this.id=id;
	this.data=data;
	
	}

   public AddressData(final String city,final String state, final String country,final String district) {
		// TODO Auto-generated constructor stub
	   this.city=city;
	   this.state=state;
	   this.country=country;
	   this.district=district;
	}



   public AddressData(final List<AddressData> data) {
	// TODO Auto-generated constructor stub
	this.addressData=data;
   }


	public AddressData(String country2, String district2) {
	// TODO Auto-generated constructor stub
		this.country = country2;
		this.district = district2;
}



	public Long getAddressTypeId() {
	return addressTypeId;
}



	public Long getAddressId() {
		return id;
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



	public Long getId() {
		return id;
	}



	public List<AddressData> getDatas() {
		return datas;
	}



	public List<String> getCountryData() {
		return countryData;
	}



	public List<String> getStateData() {
		return stateData;
	}



	public List<String> getCityData() {
		return cityData;
	}



	public String getData() {
		return data;
	}



	public List<EnumOptionData> getAddressOptionsData() {
		return addressOptionsData;
	}



	public List<AddressData> getAddressData() {
		return addressData;
	}



	public String getAddressType() {
		return addressType;
	}



	public String getStreet() {
		return street;
	}



	public String getZip() {
		return zip;
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



	public String getDistrict() {
		return district;
	}



	public List<String> getDistrictData() {
		return districtData;
	}



	public void setCityData(List<String> cityData) {
		this.cityData = cityData;
	}
	

	
	
	

}
