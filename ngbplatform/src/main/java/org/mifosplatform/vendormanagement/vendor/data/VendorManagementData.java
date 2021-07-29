/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendormanagement.vendor.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.address.data.CountryDetails;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

/**
 * Immutable data object for application user data.
 */

public class VendorManagementData {
	
	private Long id;
    private String vendorCode;
    private String vendorName;
    private String vendorEmailId;
    private String contactName;
    private String vendormobileNo;
    
    private String vendorLandlineNo;
    private String vendorAddress;
    private String vendorCountryName;
    private String vendorCurrency;
    
    private Long vendorCountryId;
    private Long vendorCurrencyId;
    private List<CountryDetails> countryData;
	private Collection<CurrencyData> currencyOptions;
    
    
    public VendorManagementData(List<CountryDetails> countryData,
			Collection<CurrencyData> currencyOptions) {
		
    	this.countryData = countryData;
    	this.currencyOptions = currencyOptions;
	}


	public VendorManagementData(Long id, String vendorCode,
			String vendorName, String vendorEmailId, String contactName,
			String vendormobileNo, String vendorLandlineNo,
			String vendorAddress, String vendorCountryName,
			Long vendorCountryId, String vendorCurrency) {
		
		this.id = id;
		this.vendorCode = vendorCode;
		this.vendorName = vendorName;
		this.vendorEmailId = vendorEmailId;
		this.contactName = contactName;
		this.vendormobileNo = vendormobileNo;
		this.vendorLandlineNo = vendorLandlineNo;
		this.vendorAddress = vendorAddress;
		this.vendorCountryName = vendorCountryName;
		this.vendorCountryId = vendorCountryId;
		this.vendorCurrency = vendorCurrency;
		
	}

	public List<CountryDetails> getCountryData() {
		return countryData;
	}

	public Collection<CurrencyData> getCurrencyOptions() {
		return currencyOptions;
	}

	public void setCountryData(List<CountryDetails> countryData) {
		this.countryData = countryData;
	}

	public void setCurrencyOptions(Collection<CurrencyData> currencyOptions) {
		this.currencyOptions = currencyOptions;
	}
	
}

