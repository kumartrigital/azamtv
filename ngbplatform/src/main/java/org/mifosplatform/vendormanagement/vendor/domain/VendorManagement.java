package org.mifosplatform.vendormanagement.vendor.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_vendor_management")
public class VendorManagement extends  AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "vendor_code")
	private String vendorCode;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	@Column(name = "vendor_emailid")
	private String vendorEmailid;
	
	@Column(name = "vendor_contact_name")
	private String vendorContactName;
	
	@Column(name = "vendor_mobile")
	private String vendorMobileNo;
	
	@Column(name = "vendor_landline")
	private String vendorLandlineNo;
	
	@Column(name = "vendor_address")
	private String vendorAddress;
	
	@Column(name = "vendor_country")
	private Long vendorCountry;

	@Column(name = "vendor_currency")
	private String vendorCurrency;
	
	@Column(name = "is_deleted")
	private String isDeleted="N";

	public  VendorManagement() {
		
	}
	
	public VendorManagement(String vendorCode, String vendorName,
			String vendorEmailId, String contactName, String vendormobileNo,
			String vendorTelephoneNo, String vendorAddress,
			Long vendorCountry,
			String vendorCurrency) {
		
		this.vendorCode = vendorCode;
		this.vendorName = vendorName;
		this.vendorEmailid = vendorEmailId;
		this.vendorContactName = contactName;
		this.vendorMobileNo = vendormobileNo;
		this.vendorLandlineNo = vendorTelephoneNo;
		this.vendorAddress = vendorAddress;
		this.vendorCountry = vendorCountry;
		this.vendorCurrency = vendorCurrency;
		
	}

	
	public static VendorManagement fromJson(final JsonCommand command) {
		
		 final String vendorCode = command.stringValueOfParameterNamed("vendorCode");
		 final String vendorName = command.stringValueOfParameterNamed("vendorName");
		 final String vendorEmailId = command.stringValueOfParameterNamed("vendorEmailId");
		 final String contactName = command.stringValueOfParameterNamed("contactName");
		 final String vendormobileNo = command.stringValueOfParameterNamed("vendormobileNo");
		 final String vendorLandlineNo = command.stringValueOfParameterNamed("vendorLandlineNo");
		 final String vendorAddress = command.stringValueOfParameterNamed("vendorAddress");
		 final Long vendorCountry = command.longValueOfParameterNamed("vendorCountry");
		 final String vendorCurrency = command.stringValueOfParameterNamed("vendorCurrency");

		 return new VendorManagement(vendorCode, vendorName, vendorEmailId, contactName,
				 vendormobileNo, vendorLandlineNo, vendorAddress, vendorCountry, vendorCurrency);
	}
	
	public Map<String, Object> update(JsonCommand command){
	
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String vendorCodeParamName = "vendorCode";
		if(command.isChangeInStringParameterNamed(vendorCodeParamName, this.vendorCode)){
			final String newValue = command.stringValueOfParameterNamed(vendorCodeParamName);
			actualChanges.put(vendorCodeParamName, newValue);
			this.vendorCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		final String vendorNameParamName = "vendorName";
		if(command.isChangeInStringParameterNamed(vendorNameParamName, this.vendorName)){
			final String newValue = command.stringValueOfParameterNamed(vendorNameParamName);
			actualChanges.put(vendorNameParamName, newValue);
			this.vendorName = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String vendorEmailIdParamName = "vendorEmailId";
		if(command.isChangeInStringParameterNamed(vendorEmailIdParamName,this.vendorEmailid)){
			final String newValue = command.stringValueOfParameterNamed(vendorEmailIdParamName);
			actualChanges.put(vendorEmailIdParamName, newValue);
			this.vendorEmailid = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String contactNameParamName = "contactName";
		if(command.isChangeInStringParameterNamed(contactNameParamName,this.vendorContactName)){
			final String newValue = command.stringValueOfParameterNamed(contactNameParamName);
			actualChanges.put(contactNameParamName, newValue);
			this.vendorContactName = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendormobileNoParamName = "vendormobileNo";
		if(command.isChangeInStringParameterNamed(vendormobileNoParamName, this.vendorMobileNo)){
			final String newValue = command.stringValueOfParameterNamed(vendormobileNoParamName);
			actualChanges.put(vendormobileNoParamName, newValue);
			this.vendorMobileNo = StringUtils.defaultIfEmpty(newValue,null); 
		}
		
		final String vendorLandlineNoParamName = "vendorLandlineNo";
		if(command.isChangeInStringParameterNamed(vendorLandlineNoParamName, this.vendorLandlineNo)){
			final String newValue = command.stringValueOfParameterNamed(vendorLandlineNoParamName);
			actualChanges.put(vendorLandlineNoParamName, newValue);
			this.vendorLandlineNo = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendorAddressParamName = "vendorAddress";
		if(command.isChangeInStringParameterNamed(vendorAddressParamName, this.vendorAddress)){
			final String newValue = command.stringValueOfParameterNamed(vendorAddressParamName);
			actualChanges.put(vendorAddressParamName, newValue);
			this.vendorAddress = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendorCountryParamName = "vendorCountry";
		if(command.isChangeInLongParameterNamed(vendorCountryParamName, this.vendorCountry)){
			final Long newValue = command.longValueOfParameterNamed(vendorCountryParamName);
			actualChanges.put(vendorCountryParamName, newValue);
			this.vendorCountry = newValue;
		}
		
		final String vendorCurrencyParamName = "vendorCurrency";
		if(command.isChangeInStringParameterNamed(vendorCurrencyParamName, this.vendorCurrency)){
			final String newValue = command.stringValueOfParameterNamed(vendorCurrencyParamName);
			actualChanges.put(vendorCurrencyParamName, newValue);
			this.vendorCurrency = StringUtils.defaultIfEmpty(newValue,null);
		}
	
		return actualChanges;
	
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {
		this.isDeleted = "Y";
	}
	
	 public boolean isDeleted() { 
		 return isDeleted.equalsIgnoreCase("Y")?true:false;
	 }
	
}