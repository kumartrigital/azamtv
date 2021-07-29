package org.mifosplatform.billing.taxmapping.domain;

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

@Entity
@Table(name = "b_tax_mapping_rate")
// , uniqueConstraints = @UniqueConstraint(name = "taxcode", columnNames = {
// "tax_code" }))
public class TaxMap extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "charge_code", length = 10, nullable = false)
	private String chargeCode;

	@Column(name = "tax_code", length = 10, nullable = false)
	private String taxCode;

	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "type", length = 15, nullable = false)
	private String taxType;

	@Column(name = "rate", nullable = false)
	private BigDecimal rate;

	@Column(name = "tax_region_id", nullable = false)
	private Long taxRegion;
	
	@Column(name="end_date")
	private Date enddate;
	
	@Column(name="is_deleted")
	private char isDeleted;

	public TaxMap() {
	}

	public TaxMap(final String chargeCode, final String taxCode,
			final LocalDate startDate, final String taxType, final BigDecimal rate,
			final Long taxRegion) {
		this.chargeCode = chargeCode;
		this.taxCode = taxCode;
		this.startDate = startDate.toDate();
		this.taxType = taxType;
		this.rate = rate;
		this.taxRegion = taxRegion;
		this.isDeleted = 'N';
	}

	public static TaxMap fromJson(final JsonCommand command) {

		final String chargeCode = command.stringValueOfParameterNamed("chargeCode");
		final String taxCode = command.stringValueOfParameterNamed("taxCode");
		final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
		final String taxType = command.stringValueOfParameterNamed("taxType");
		final BigDecimal rate = command.bigDecimalValueOfParameterNamed("rate");
		final Long taxRegion = command.longValueOfParameterNamed("taxRegion");

		return new TaxMap(chargeCode, taxCode, startDate, taxType,
				rate, taxRegion);
	}

	public Map<String, Object> update(JsonCommand command) {
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(
				1);
		if (command.isChangeInStringParameterNamed("chargeCode",this.chargeCode)) {
			final String newValue = command.stringValueOfParameterNamed("chargeCode");
			actualChanges.put("chargeCode", newValue);
			this.chargeCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed("taxCode", this.taxCode)) {
			final String newValue = command.stringValueOfParameterNamed("taxCode");
			actualChanges.put("taxCode", newValue);
			this.taxCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInDateParameterNamed("startDate", this.startDate)) {
			final LocalDate newValue = command.localDateValueOfParameterNamed("startDate");
			actualChanges.put("startDate", newValue);
			this.startDate = newValue.toDate();

		}
		if (command.isChangeInStringParameterNamed("taxType", this.taxType)) {
			final String newValue = command.stringValueOfParameterNamed("taxType");
			actualChanges.put("taxType", newValue);
			this.taxType = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInBigDecimalParameterNamed("rate", this.rate)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed("rate");
			actualChanges.put("rate", newValue);
			this.rate = newValue;
		}
		if (command.isChangeInLongParameterNamed("taxRegion", this.taxRegion)) {
			final Long newValue = command.longValueOfParameterNamed("taxRegion");
			actualChanges.put("rate", newValue);
			this.taxRegion = newValue;
		}

		return actualChanges;
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}
	
	public void setChargeCode(final String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the type
	 */
	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(final String taxType) {
		this.taxType = taxType;
	}

	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {
		Date endDate = new Date();
		this.enddate = endDate;
	this.isDeleted = 'Y';
	}

}
