package org.mifosplatform.finance.chargeorder.data;

import java.math.BigDecimal;
import java.util.Date;

import org.mifosplatform.finance.chargeorder.domain.BillItem;

public class GenerateChargeData {
	
	private final Long clientId;
	private final Date nextBillableDay;
	private BigDecimal invoiceAmount;
	private BillItem invoice;
	
	public GenerateChargeData( final Long clientId, final Date nextBillableDay,BigDecimal invoiceAmount,BillItem invoice) {
		this.clientId = clientId;
		this.nextBillableDay = nextBillableDay;
		this.invoiceAmount=invoiceAmount;
		this.invoice = invoice;
	}

	public Long getClientId() {
		return clientId;
	}

	public Date getNextBillableDay() {
		return nextBillableDay;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public BillItem getInvoice() {
		return invoice;
	}

}
