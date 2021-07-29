package org.mifosplatform.billing.currency.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author hugo
 * 
 *         this class help to interact with data table 'b_country_currency'
 */
@Entity
@Table(name = "b_currency_exchange" /*, uniqueConstraints = @UniqueConstraint(name = "country_key", columnNames = { "country" })*/)
public class CountryCurrency extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "currency")
	private Long currency;

	@Column(name = "status")
	private String status;

	@Column(name = "base_currency")
	private Long baseCurrency;

	@Column(name = "conversion_rate", scale = 6, precision = 19, nullable = false)
	private BigDecimal conversionRate;

	@Column(name = "is_deleted")
	private char isDeleted = 'N';
	
	@Column(name = "valid_from")
	private Date validFrom;
	
	@Column(name = "valid_to")
	private Date validTo;

	public CountryCurrency() {
	}

	public CountryCurrency(final String country, final Long currency,
			final Long basecurrency, final BigDecimal conversionRate,
			final String status,final String countryISD,final LocalDate validFrom,final LocalDate validTo)

	{
		//this.country = country;
		this.currency = currency;
		this.status = status;
		this.baseCurrency = basecurrency;
		this.conversionRate = conversionRate;
		//this.countryISD = countryISD;
		this.validFrom = validFrom.toDate();
		this.validTo = validTo!=null?validTo.toDate():null;

	}

	/**
	 * @return the country
	 */
	

	/**
	 * @return the currency
	 */
	public Long getCurrency() {
		return currency;
	}

	public void setCurrency(Long currency) {
		this.currency = currency;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the baseCurrency
	 */
	public Long getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Long baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the conversionRate
	 */
	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}


	public Date getValidFrom() {
		return validFrom;
	}
	
	public void setValidFrom(LocalDate validFrom) {

		this.validFrom = validFrom.toDate();
	}
	
	public Date getValidTo(){
		return validTo;
	}
	
	public void setValidTo(LocalDate validTo){
		this.validTo = validTo.toDate();
	}
	/**
	 * updating column 'is_deleted' with 'Y' for delete of country currency
	 * configuration
	 */

	public void delete() {

	if (this.isDeleted == 'N') {
		this.isDeleted = 'Y';
	}
	}

	/**
	 * @param command
	 * @return CountryCurrency
	 */
	public static CountryCurrency fromJson(final JsonCommand command) {

		final String country = command.stringValueOfParameterNamed("country");
		final Long currency = command.longValueOfParameterNamed("currency");
		final String status = command.stringValueOfParameterNamed("status");
		final Long baseCurrency = command
				.longValueOfParameterNamed("baseCurrency");
		final BigDecimal conversionRate = command
				.bigDecimalValueOfParameterNamed("conversionRate");
		final String countryISD = command.stringValueOfParameterNamed("countryISD");
		final LocalDate validFrom = command.localDateValueOfParameterNamed("validFrom");
		final LocalDate validTo = command.localDateValueOfParameterNamed("validTo");
		
		return new CountryCurrency(country, currency, baseCurrency,
				conversionRate, status, countryISD,validFrom,validTo);

	}

	/**
	 * @param command
	 * @return changes of CountryCurrency object
	 */
	public Map<String, Object> update(JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(
				1);


		if (command.isChangeInLongParameterNamed("currency", this.currency)) {
			final Long newValue = command
					.longValueOfParameterNamed("currency");
			actualChanges.put("currency", newValue);
			this.currency = newValue;
		}

		if (command.isChangeInStringParameterNamed("status", this.status)) {
			final String newValue = command
					.stringValueOfParameterNamed("status");
			actualChanges.put("status", newValue);
			this.status = StringUtils.defaultIfEmpty(newValue, null);
		}

		if (command.isChangeInLongParameterNamed("baseCurrency",
				this.baseCurrency)) {
			final Long newValue = command
					.longValueOfParameterNamed("baseCurrency");
			actualChanges.put("baseCurrency", newValue);
			this.baseCurrency = newValue;
		}

		if (command.isChangeInBigDecimalParameterNamed("conversionRate",
				this.conversionRate)) {
			final BigDecimal newValue = command
					.bigDecimalValueOfParameterNamed("conversionRate");
			actualChanges.put("conversionRate", newValue);
			this.conversionRate = newValue;
		}
		
		
		if (command.isChangeInLocalDateParameterNamed("validFrom",new LocalDate(this.validFrom))) {
			final LocalDate newValue = command.localDateValueOfParameterNamed("validFrom");
			actualChanges.put("validFrom", newValue);
			this.validFrom=newValue.toDate();
		}
		
		if (command.isChangeInLocalDateParameterNamed("validTo",new LocalDate(this.validTo))) {
			final LocalDate newValue = command.localDateValueOfParameterNamed("validTo");
			actualChanges.put("validTo", newValue);
			if(newValue!=null)
				this.validTo=newValue.toDate();
		}


		return actualChanges;

	}
}
