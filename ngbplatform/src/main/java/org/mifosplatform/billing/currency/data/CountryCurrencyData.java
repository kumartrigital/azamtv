package org.mifosplatform.billing.currency.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;

/**
 * @author hugo
 * 
 */
public class CountryCurrencyData {

	private Long id;
	private String country;
	private String currency;
	private String baseCurrency;
	private BigDecimal conversionRate;
	private String status;
	private String countryISD;
	private LocalDate validFrom;
	private LocalDate validTo;
	private ApplicationCurrencyConfigurationData currencydata;
	private List<String> countryData;
	private List<EnumOptionData> statusData;
	private String currencyCode;
	private String baseCurrencyCode;

	public CountryCurrencyData(final Long id,
			final String currency, final String baseCurrency,
			final BigDecimal conversionRate, final String status,final LocalDate validFrom,
			final LocalDate validTo,final String currencyCode,final String baseCurrencyCode) {

		this.id = id;
		this.currency = currency;
		this.baseCurrency = baseCurrency;
		this.conversionRate = conversionRate;
		this.status = status;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.currencyCode =currencyCode;
		this.baseCurrencyCode =baseCurrencyCode;
		

	}
	
	public CountryCurrencyData() {
	}

	public CountryCurrencyData(final CountryCurrencyData currencyData,
			final ApplicationCurrencyConfigurationData currency,
			final List<String> countryData,
			final List<EnumOptionData> statusData) {

		if (currencyData != null) {
			this.id = currencyData.getId();
		    this.country = currencyData.getCountry();
			this.currency = currencyData.getCurrency();
			this.status = currencyData.getStatus();
			this.baseCurrency = currencyData.getBaseCurrency();
			this.conversionRate = currencyData.getConversionRate();
			this.countryISD = currencyData.getCountryISD();
			this.validFrom = currencyData.getValidFrom();
			this.validTo = currencyData.getValidTo();
		}

		this.currencydata = currency;
		this.countryData = countryData;
		this.statusData = statusData;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @return the conversionRate
	 */
	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the currencydata
	 */
	public ApplicationCurrencyConfigurationData getCurrencydata() {
		return currencydata;
	}

	/**
	 * @return the countryData
	 */
	public List<String> getCountryData() {
		return countryData;
	}

	/**
	 * @return the statusData
	 */
	public List<EnumOptionData> getStatusData() {
		return statusData;
	}

	public String getCountryISD() {
		return countryISD;
	}
	
	public LocalDate getValidFrom() {
		return validFrom;
	}
	
	public LocalDate getValidTo() {
		return validTo;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public void setCurrencydata(ApplicationCurrencyConfigurationData currencydata) {
		this.currencydata = currencydata;
	}

	public void setCountryData(List<String> countryData) {
		this.countryData = countryData;
	}

	public void setStatusData(List<EnumOptionData> statusData) {
		this.statusData = statusData;
	}
	
	
	
}
