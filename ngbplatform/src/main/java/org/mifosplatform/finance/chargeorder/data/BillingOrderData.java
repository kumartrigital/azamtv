package org.mifosplatform.finance.chargeorder.data;

import java.math.BigDecimal;
import java.util.Date;

public class BillingOrderData {

	private Long clientOrderId;
	private Long OderPriceId;
	private Long planId;
	private Long clientId;
	private Date startDate;
	private Date nextBillableDate;
	private Date endDate;
	private String billingFrequency;
	private String chargeCode;
	private String chargeType;
	private Integer chargeDuration;
	private String durationType;
	private Date invoiceTillDate;
	private BigDecimal price;
	private String billingAlign;
	private Date billStartDate;
	private Date billEndDate;
	private Long orderstatus;
	private Long orderId;
	private Integer taxInclusive;
	private Long invoiceId;
	private Boolean taxExemption;
	private Boolean isAggregate;
	private Long currencyId;
	private String chargeOwner;
	

	public BillingOrderData(final Long orderId,final String durationType,final Date startDate, final Date nextBillableDate, final Date invoiceTillDate, 
			final String billingAlign, final String chargeCode,final String chargeOwner){
		
		this.orderId = orderId;
		this.durationType = durationType;
		this.startDate = startDate;
		this.nextBillableDate=nextBillableDate;
		this.invoiceTillDate = invoiceTillDate;
		this.billingAlign = billingAlign;
		this.chargeCode = chargeCode;
		this.chargeOwner = chargeOwner;
	}

	

	public BillingOrderData(final Long orderId,final String durationType,final Date startDate, final Date nextBillableDate, final Date invoiceTillDate, 
			final String billingAlign, final String chargeCode,final String chargeOwner,final Long planId){
		
		this.orderId = orderId;
		this.durationType = durationType;
		this.startDate = startDate;
		this.nextBillableDate=nextBillableDate;
		this.invoiceTillDate = invoiceTillDate;
		this.billingAlign = billingAlign;
		this.chargeCode = chargeCode;
		this.chargeOwner = chargeOwner;
		this.planId = planId;
	}
	
	
	public BillingOrderData(final Long clientOrderId,final Long OderPriceId,Long planId,final Long clientId,final Date startDate,
			final Date nextBillableDate,final Date endDate,final String billingFrequency,final String chargeCode,final String chargeType,
			final Integer chargeDuration,final String durationType,final Date invoiceTillDate,final BigDecimal price,final String billingAlign,
			final Date billStartDate,final Date billEndDate,final Long orderstatus,final Integer taxInclusive,final String taxExemption,
			final Boolean isAggregate,final Long currencyId,final String chargeOwner) {
		
		this.clientOrderId = clientOrderId;
		this.OderPriceId = OderPriceId;
		this.planId = planId;
		this.clientId = clientId;
		this.startDate = startDate;
		this.nextBillableDate = nextBillableDate;
		this.endDate = endDate;
		this.billingFrequency = billingFrequency;
		this.chargeCode = chargeCode;
		this.chargeType = chargeType;
		this.chargeDuration = chargeDuration;
		this.durationType = durationType;
		this.invoiceTillDate = invoiceTillDate;
		this.price = price;
		this.billingAlign = billingAlign;
		this.billStartDate = billStartDate;
		this.billEndDate = billEndDate;
		this.orderstatus=orderstatus;
		this.taxInclusive = taxInclusive;
		this.taxExemption = "Y".equalsIgnoreCase(taxExemption) ? true : false;
		this.isAggregate = isAggregate;
		this.currencyId = currencyId;
		this.chargeOwner = chargeOwner;

	}
	
	
	public BillingOrderData(final Long itemId,final Long clientId,final Date startDate,final String chargeCode,final String chargeType,
			final BigDecimal price,final Integer taxInclusive,final Long currencyId,final String chargeOwner){
		
		this.clientOrderId = itemId;
		this.clientId = clientId;
		this.billStartDate = startDate;
		this.chargeCode = chargeCode;
		this.chargeType = chargeType;
		this.price = price;
		this.taxInclusive = taxInclusive;
		this.currencyId = currencyId;
		this.chargeOwner = chargeOwner;
		
	}

	public BillingOrderData(final Long clientOderId, final Long orderPriceId, final Long planId,final Long clientId, final Date startDate, final Date nextBillableDate,
			final Date endDate, final String billingFrequency, final String chargeCode,final String chargeType, final Integer chargeDuration, final String durationType,
			final Date invoiceTillDate, final BigDecimal price, final String billingAlign,final Date billStartDate, final Date billEndDate, final Long orderStatus,
			final Integer taxInclusive, final Long invoiceId,final Long currencyId,final String chargeOwner) {
		
		this.clientOrderId=clientOderId;
		this.OderPriceId=orderPriceId;
		this.planId=planId;
		this.clientId=clientId;
		this.startDate=startDate;
		this.nextBillableDate=nextBillableDate;
		this.endDate=endDate;
		this.billingFrequency=billingFrequency;
		this.chargeCode=chargeCode;
		this.chargeType=chargeType;
		this.chargeDuration=chargeDuration;
		this.durationType=durationType;
		this.invoiceTillDate=invoiceTillDate;
		this.price=price;
		this.billingAlign=billingAlign;
		this.billStartDate=billStartDate;
		this.billEndDate=billEndDate;
		this.orderstatus=orderStatus;
		this.taxInclusive=taxInclusive;
		this.invoiceId=invoiceId;
		this.currencyId=currencyId;
		this.chargeOwner = chargeOwner;
		
	}


	public String getChargeOwner() {
		return chargeOwner;
	}

	public void setChargeOwner(String chargeOwner) {
		this.chargeOwner = chargeOwner;
	}

	public BillingOrderData(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getNextBillableDate() {
		return nextBillableDate;
	}

	public void setNextBillableDate(Date nextBillableDate) {
		this.nextBillableDate = nextBillableDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public Integer getChargeDuration() {
		return chargeDuration;
	}

	public void setChargeDuration(Integer chargeDuration) {
		this.chargeDuration = chargeDuration;
	}

	public String getDurationType() {
		return durationType;
	}

	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}

	public Date getInvoiceTillDate() {
		return invoiceTillDate;
	}

	public void setInvoiceTillDate(Date invoiceTillDate) {
		this.invoiceTillDate = invoiceTillDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getBillingAlign() {
		return billingAlign;
	}

	public void setBillingAlign(String billingAlign) {
		this.billingAlign = billingAlign;
	}

	public Long getClientOrderId() {
		return clientOrderId;
	}

	public void setClientOrderId(Long clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

	public Long getOderPriceId() {
		return OderPriceId;
	}

	public void setOderPriceId(Long oderPriceId) {
		OderPriceId = oderPriceId;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public Date getBillStartDate() {
		return billStartDate;
	}

	public void setBillStartDate(Date billStartDate) {
		this.billStartDate = billStartDate;
	}

	public Date getBillEndDate() {
		return billEndDate;
	}

	public void setBillEndDate(Date billEndDate) {
		this.billEndDate = billEndDate;
	}

	public Long getOrderStatus() {
		return orderstatus;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getTaxInclusive() {
		return taxInclusive;
	}

	public void setTaxInclusive(Integer taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public boolean isTaxExemption() {
		return taxExemption;
	}

	public void setTaxExemption(Boolean taxExemption) {
		this.taxExemption = taxExemption;
	}

	public Boolean getTaxExemption() {
		return taxExemption;
	}

	public Boolean getIsAggregate() {
		return isAggregate;
	}
	
	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	
}
