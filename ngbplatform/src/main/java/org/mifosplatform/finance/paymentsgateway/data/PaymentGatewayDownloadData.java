/**
 * 
 */
package org.mifosplatform.finance.paymentsgateway.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

/**
 * @author rakesh
 *
 */
public class PaymentGatewayDownloadData {

	
	private String serialNo;
	private LocalDate paymendDate;
	private BigDecimal amountPaid;
	private String phoneMSISDN;
	private String remarks;
	private String status;
	private String receiptNo;
	private String paymentId;

	public PaymentGatewayDownloadData(final String serialNumber, final LocalDate paymentDate,
			final BigDecimal amountPaid, final String phoneMSISDN, final String remarks,
			final String status, final String receiptNo, final String paymentId) {
		this.serialNo = serialNumber;
		this.paymendDate = paymentDate;
		this.amountPaid = amountPaid;
		this.phoneMSISDN = phoneMSISDN;
		this.remarks = remarks;
		this.status = status;
		this.receiptNo = receiptNo;
		this.paymentId = paymentId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public LocalDate getPaymendDate() {
		return paymendDate;
	}

	public void setPaymendDate(LocalDate paymendDate) {
		this.paymendDate = paymendDate;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getPhoneMSISDN() {
		return phoneMSISDN;
	}

	public void setPhoneMSISDN(String phoneMSISDN) {
		this.phoneMSISDN = phoneMSISDN;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	
}
