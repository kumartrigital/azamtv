package org.mifosplatform.billing.chargecode.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author hugo
 * 
 */
@Entity
@Table(name = "b_charge_codes", uniqueConstraints = {
		@UniqueConstraint(name = "chargecode", columnNames = { "charge_code" }),
		@UniqueConstraint(name = "chargedescription", columnNames = { "charge_description" }) })
public class ChargeCodeMaster extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "charge_code")
	private String chargeCode;

	@Column(name = "charge_description")
	private String chargeDescription;

	@Column(name = "charge_type")
	private String chargeType;

	@Column(name = "charge_duration")
	private Integer chargeDuration;

	@Column(name = "duration_type")
	private String durationType;

	@Column(name = "tax_inclusive")
	private Integer taxInclusive;

	@Column(name = "billfrequency_code")
	private String billFrequencyCode;
	
	@Column(name = "is_aggregate")
	private Integer isAggregate;

	public ChargeCodeMaster() {
	}

	public ChargeCodeMaster(final String chargeCode, final String chargeDescription,
			final String chargeType, final Integer chargeDuration,
			final String durationType, final Integer taxInclusive,
			final String billFrequencyCode,final Integer isAggregate) {

		this.chargeCode = chargeCode;
		this.chargeDescription = chargeDescription;
		this.chargeType = chargeType;
		this.chargeDuration = chargeDuration;
		this.durationType = durationType;
		this.taxInclusive = taxInclusive;
		this.billFrequencyCode = billFrequencyCode;
		this.isAggregate = isAggregate;
	}

	/**
	 * @param command
	 * @return chargeCode constructor
	 */
	public static ChargeCodeMaster fromJson(final JsonCommand command) {

		final String chargeCode = command
				.stringValueOfParameterNamed("chargeCode");
		final String chargeDescription = command
				.stringValueOfParameterNamed("chargeDescription");
		final Integer chargeDuration = command
				.integerValueOfParameterNamed("chargeDuration");
		final String chargeType = command
				.stringValueOfParameterNamed("chargeType");
		final String durationType = command
				.stringValueOfParameterNamed("durationType");
		final boolean taxInclusive = command
				.booleanPrimitiveValueOfParameterNamed("taxInclusive");

		Integer tax = null;

		if (taxInclusive) {
			tax = 1;
		} else {
			tax = 0;
		}

		final String billFrequencyCode = command
				.stringValueOfParameterNamed("billFrequencyCode");
		
		final boolean isAggregate = command
				.booleanPrimitiveValueOfParameterNamed("isAggregate");
		

		Integer aggregate = null;

		if (isAggregate) {
			aggregate = 1;
		} else {
			aggregate = 0;
		}

		return new ChargeCodeMaster(chargeCode, chargeDescription, chargeType,
				chargeDuration, durationType, tax, billFrequencyCode,aggregate);
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the chargeDescription
	 */
	public String getChargeDescription() {
		return chargeDescription;
	}

	public void setChargeDescription(String chargeDescription) {
		this.chargeDescription = chargeDescription;
	}

	/**
	 * @return the chargeType
	 */
	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	/**
	 * @return the chargeDuration
	 */
	public Integer getChargeDuration() {
		return chargeDuration;
	}

	public void setChargeDuration(Integer chargeDuration) {
		this.chargeDuration = chargeDuration;
	}

	/**
	 * @return the durationType
	 */
	public String getDurationType() {
		return durationType;
	}

	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}

	/**
	 * @return the taxInclusive
	 */
	public Integer getTaxInclusive() {
		return taxInclusive;
	}

	public void setTaxInclusive(Integer taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	/**
	 * @return the billFrequencyCode
	 */
	public String getBillFrequencyCode() {
		return billFrequencyCode;
	}

	public void setBillFrequencyCode(String billFrequencyCode) {
		this.billFrequencyCode = billFrequencyCode;
	}
	
	public Integer getIsAggregate() {
		return isAggregate;
	}
	
	public void setIsAggregate(Integer isAggregate) {
		this.isAggregate = isAggregate;
	}

	/**
	 * @param command
	 * @return changes of chargeCode object
	 */
	public Map<String, Object> update(JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(
				1);
		if (command.isChangeInStringParameterNamed("chargeCode",
				this.chargeCode)) {
			final String newValue = command
					.stringValueOfParameterNamed("chargeCode");
			actualChanges.put("chargeCode", newValue);
			this.chargeCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed("chargeDescription",
				this.chargeDescription)) {
			final String newValue = command
					.stringValueOfParameterNamed("chargeDescription");
			actualChanges.put("chargeDescription", newValue);
			this.chargeDescription = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed("chargeType",
				this.chargeType)) {
			final String newValue = command
					.stringValueOfParameterNamed("chargeType");
			actualChanges.put("chargeType", newValue);
			this.chargeType = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInIntegerParameterNamed("chargeDuration",
				this.chargeDuration)) {
			final Integer newValue = command
					.integerValueOfParameterNamed("chargeDuration");
			actualChanges.put("chargeDuration", newValue);
			this.chargeDuration = newValue;
		}
		if (command.isChangeInStringParameterNamed("durationType",
				this.durationType)) {
			final String newValue = command
					.stringValueOfParameterNamed("durationType");
			actualChanges.put("durationType", newValue);
			this.durationType = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInBooleanParameterNamed("taxInclusive",
				this.taxInclusive == 1 ? true : false)) {
			final boolean taxInclusive = command
					.booleanPrimitiveValueOfParameterNamed("taxInclusive");

			Integer newValue = null;

			if (taxInclusive) {
				newValue = 1;
			} else {
				newValue = 0;
			}
			actualChanges.put("taxInclusive", newValue);
			this.taxInclusive = newValue;
		}
		if (command.isChangeInStringParameterNamed("billFrequencyCode",
				this.billFrequencyCode)) {
			final String newValue = command
					.stringValueOfParameterNamed("billFrequencyCode");
			actualChanges.put("billFrequencyCode", newValue);
			this.billFrequencyCode = StringUtils.defaultIfEmpty(newValue, null);

		}
		if (command.isChangeInBooleanParameterNamed("isAggregate",
				this.isAggregate == 1 ? true : false)) {
			final boolean isAggregate = command
					.booleanPrimitiveValueOfParameterNamed("isAggregate");

			Integer newValue = null;

			if (isAggregate) {
				newValue = 1;
			} else {
				newValue = 0;
			}
			actualChanges.put("isAggregate", newValue);
			this.isAggregate = newValue;
		}

		return actualChanges;
	}

}
