package org.mifosplatform.finance.chargeorder.data;

import java.math.BigDecimal;

public class ChargeTaxCommand {

	private final Long clientId;
	private final Long invoiceChargeId;
	private final Long invoiceId;
	private final String taxCode;
	private final Integer taxValue;
	private final BigDecimal taxPercentage;
	private final BigDecimal taxAmount;
	private final BigDecimal discountedAmount;

	public ChargeTaxCommand(final Long clientId, final Long invoiceChargeId,
			final Long invoiceId, final String taxCode,
			final Integer taxValue, final BigDecimal taxPercentage,
			final BigDecimal taxAmount, BigDecimal price) {

		this.clientId = clientId;
		this.invoiceChargeId = invoiceChargeId;
		this.invoiceId = invoiceId;
		this.taxCode = taxCode;
		this.taxValue = taxValue;
		this.taxPercentage = taxPercentage;
		this.taxAmount = taxAmount;
		this.discountedAmount  =price;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getInvoiceChargeId() {
		return invoiceChargeId;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public Integer getTaxValue() {
		return taxValue;
	}

	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getDiscountedAmount() {
		return discountedAmount;
	}

	
}
