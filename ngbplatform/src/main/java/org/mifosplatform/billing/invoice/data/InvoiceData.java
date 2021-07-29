package org.mifosplatform.billing.invoice.data;

import java.math.BigDecimal;
import java.util.Date;
public class InvoiceData {
	
	
	 Long id;
	 private BigDecimal amount;
	 private BigDecimal dueAmount;
	 private Date billDate;
	 Long billId;
	 public InvoiceData(Long id,BigDecimal amount,BigDecimal dueAmount,Date billDate,Long billId) {

			this.id=id;
			this.amount=amount;
			this.dueAmount=dueAmount;
			this.billDate=billDate;
			this.billId=billId;
		}
	 
	 public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getDiscountAmount() {
		return dueAmount;
	}

	public void setDiscountAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}
	
}
