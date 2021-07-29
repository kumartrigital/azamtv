package org.mifosplatform.crm.clientprospect.data;

import java.math.BigDecimal;

public class QuoteData {
	private Long quoteId;
	private Long leadId;
	private String status;
	private String planName;
	private BigDecimal planRecurirngCharge;
	private BigDecimal planOnetimeCharge;
	private BigDecimal totalCharge;
	private Long planId;
	private Long serviceId;
	private String chargeCode;
	private BigDecimal recurringCharge;
	private BigDecimal oneTimeCharge;
	private Long currencyId;
	private String planCode;
	private String planDescription;
	private String serviceCode;
	private String quoteNumber;
	private String frequency;
	private String Notes;
	
	public QuoteData() {
	}
	public QuoteData(Long planId, Long serviceId,String planCode,String planDescription) {
		this.planId = planId;
		this.serviceId = serviceId;
		this.planCode = planCode;
		this.planDescription = planDescription;

	}
	public QuoteData(Long planId, String chargeCode, BigDecimal recurringCharge, BigDecimal oneTimeCharge,
			Long currencyId) {
		this.planId = planId;
		this.chargeCode = chargeCode;
		this.recurringCharge = recurringCharge;
		this.oneTimeCharge = oneTimeCharge;
		this.currencyId = currencyId;

	}
	public QuoteData(String quoteNumber,Long quoteId, Long leadId, String status, String serviceCode, String planDescription,
			BigDecimal recurringCharge, BigDecimal oneTimeCharge, BigDecimal totalCharge,Long serviceId, Long planId,String chargeCode,String  frequency,String Notes) {
		this.quoteNumber = quoteNumber;
		this.quoteId = quoteId;
		this.leadId = leadId;
		this.status = status;
		this.serviceCode = serviceCode;
		this.planDescription = planDescription;
		this.recurringCharge = recurringCharge;
		this.oneTimeCharge = oneTimeCharge;
		this.totalCharge = totalCharge;
		this.serviceId = serviceId;
		this.planId = planId;
		this.chargeCode = chargeCode;
		this.frequency = frequency;
		this.Notes = Notes;
	}
	public QuoteData(String quoteNumber, Long quoteId,String status) {
		this.quoteNumber = quoteNumber;
		this.quoteId = quoteId;
		this.status = status;
	}
	public String getNotes() {
		return Notes;
	}
	public void setNotes(String notes) {
		Notes = notes;
	}
	
	
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	public BigDecimal getRecurringCharge() {
		return recurringCharge;
	}
	public void setRecurringCharge(BigDecimal recurringCharge) {
		this.recurringCharge = recurringCharge;
	}
	public BigDecimal getOneTimeCharge() {
		return oneTimeCharge;
	}
	public void setOneTimeCharge(BigDecimal oneTimeCharge) {
		this.oneTimeCharge = oneTimeCharge;
	}
	public Long getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	public String getPlanCode() {
		return planCode;
	}
	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}
	public String getPlanDescription() {
		return planDescription;
	}
	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}
	public String getQuoteNumber() {
		return quoteNumber;
	}
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public Long getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(Long quoteId) {
		this.quoteId = quoteId;
	}
	public Long getLeadId() {
		return leadId;
	}
	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public BigDecimal getPlanRecurirngCharge() {
		return planRecurirngCharge;
	}
	public void setPlanRecurirngCharge(BigDecimal planRecurirngCharge) {
		this.planRecurirngCharge = planRecurirngCharge;
	}
	public BigDecimal getPlanOnetimeCharge() {
		return planOnetimeCharge;
	}
	public void setPlanOnetimeCharge(BigDecimal planOnetimeCharge) {
		this.planOnetimeCharge = planOnetimeCharge;
	}
	public BigDecimal getTotalCharge() {
		return totalCharge;
	}
	public void setTotalCharge(BigDecimal totalCharge) {
		this.totalCharge = totalCharge;
	}

	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

}
