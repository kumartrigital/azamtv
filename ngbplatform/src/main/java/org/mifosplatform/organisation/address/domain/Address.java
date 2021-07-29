package org.mifosplatform.organisation.address.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

@Entity
@Table(name = "b_client_address")
public class Address{


	@Id
	@GeneratedValue
	@Column(name="address_id")
	private Long id;

	@Column(name = "client_id", length = 65536)
	private Long clientId;

	@Column(name = "address_no")
	private String addressNo;
	
	@Column(name = "address_key")
	private String addressKey;

	@Column(name = "street")
	private String street;
	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "zip")
	private String zip;
	
	@Column(name = "phone_num")
	private String phone;
	
	@Column(name = "email")
	private String email;

	@Column(name = "is_deleted", nullable = false)
	private  char deleted='n';
	
	@Column(name = "district")
	private String district;

	

	public Address() {
         
          
	}
	


	public Address(final Long clientId,final String addressKey, final String addressNo, final String street,
			final String city, final String state, final String country, final String zip, final String phone, final String email,final String district) {
		
		
		 this.clientId=clientId;
		 this.addressKey=addressKey;
         this.addressNo=addressNo;
         this.street=street;
         this.addressKey=addressKey;
         this.city=city;
         this.state=state;
         this.country=country;
         this.zip=zip;
         this.phone=phone;
         this.email=email;
         this.district=district;
	}

		public static Address fromJson(final Long clientId, final JsonCommand command) {
			 String addressKey = command.stringValueOfParameterNamed("addressType");
			  addressKey=addressKey.isEmpty()?"PRIMARY":addressKey;
			    final String addressNo = command.stringValueOfParameterNamed("addressNo");
			    final String street = command.stringValueOfParameterNamed("street");
			    final String city = command.stringValueOfParameterNamed("city");
			    final String zip = command.stringValueOfParameterNamed("zipCode");
			    final String state = command.stringValueOfParameterNamed("state");
			    final String country = command.stringValueOfParameterNamed("country");
			    final String phone = command.stringValueOfParameterNamed("phone");
			    final String email = command.stringValueOfParameterNamed("email");
			    final String district = command.stringValueOfParameterNamed("district");
			    return new Address(clientId, addressKey, addressNo, street, city, state, country, zip,phone,email,district);
			}
		
       public Address(final Long clientId,final String addressKey,final String addressNo, final String street,
				final String city, final String state, final String country,final String district,final String zip) {
    	
    	   this.clientId=clientId;  
    	 this.addressKey=addressKey;
    	 this.addressNo=addressNo;
         this.street=street;
         this.city=city;
         this.state=state;
         this.country=country;
         this.district=district;
         this.zip=zip;
          }
	

		public Long getId() {
			return id;
		}



		public Map<String, Object> update(final JsonCommand command) {

			  final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
			  final String firstnameParamName = "addressType";
		        if (command.isChangeInStringParameterNamed(firstnameParamName, this.addressKey)) {
		            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
		            actualChanges.put(firstnameParamName, newValue);
		            this.addressKey = StringUtils.defaultIfEmpty(newValue, null);
		        }

		        final String addressNoParamName = "addressNo";
		        if (command.isChangeInStringParameterNamed(addressNoParamName, this.addressNo)) {
		            final String newValue = command.stringValueOfParameterNamed(addressNoParamName);
		            actualChanges.put(addressNoParamName, newValue);
		            this.addressNo = StringUtils.defaultIfEmpty(newValue, null);
		        }
		        final String streetParamName = "street";
				if (command.isChangeInStringParameterNamed(streetParamName,	this.street)) {
					final String newValue = command.stringValueOfParameterNamed(streetParamName);
					actualChanges.put(streetParamName, newValue);
					this.street=StringUtils.defaultIfEmpty(newValue, null);
				}

		  final String zipParamName = "zipCode";
	        if (command.isChangeInStringParameterNamed(zipParamName, this.zip)) {
	            final String newValue = command.stringValueOfParameterNamed(zipParamName);
	            actualChanges.put(zipParamName, newValue);
	            this.zip = StringUtils.defaultIfEmpty(newValue, null);
	        }

	        final String cityParamName = "city";
	        if (command.isChangeInStringParameterNamed(cityParamName, this.city)) {
	            final String newValue = command.stringValueOfParameterNamed(cityParamName);
	            actualChanges.put(cityParamName, newValue);
	            this.city = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        final String stateParamName = "state";
			if (command.isChangeInStringParameterNamed(stateParamName,this.state)) {
				final String newValue = command.stringValueOfParameterNamed(stateParamName);
				actualChanges.put(stateParamName, newValue);
				this.state=StringUtils.defaultIfEmpty(newValue,null);
			}
	        final String countryParamName = "country";
			if (command.isChangeInStringParameterNamed(countryParamName,this.country)) {
				final String newValue = command.stringValueOfParameterNamed(countryParamName);
				actualChanges.put(countryParamName, newValue);
				this.country=StringUtils.defaultIfEmpty(newValue,null);
			}
			
			 final String districtParamName = "district";
		        if (command.isChangeInStringParameterNamed(districtParamName, this.district)) {
		            final String newValue = command.stringValueOfParameterNamed(districtParamName);
		            actualChanges.put(districtParamName, newValue);
		            this.district = StringUtils.defaultIfEmpty(newValue, null);
		        }
			
	        return actualChanges;

		}


	public Long getClientId() {
		return clientId;
	}

	public String getAddressNo() {
		return addressNo;
	}

	public String getAddressKey() {
		return addressKey;
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

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public char getDeleted() {
		return deleted;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public void setAddressNo(String addressNo) {
		this.addressNo = addressNo;
	}

	public void setAddressKey(String addressKey) {
		this.addressKey = addressKey;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public void delete() {

		this.deleted = 'Y';
				
	}



	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}
	
		
	
	}
	
	
			

	


