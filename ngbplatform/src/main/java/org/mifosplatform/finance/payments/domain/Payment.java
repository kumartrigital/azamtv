package org.mifosplatform.finance.payments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.useradministration.domain.AppUser;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_payments", uniqueConstraints = {
		@UniqueConstraint(name = "receipt_no", columnNames = { "receipt_no" }) })
public class Payment extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "client_id", nullable = false)
	private Long clientId;

	@Column(name = "amount_paid", scale = 6, precision = 19, nullable = false)
	private BigDecimal amountPaid;

	@Column(name = "bill_id", nullable = false)
	private Long statementId;

	@Column(name = "is_deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "payment_date")
	private Date paymentDate;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "paymode_id")
	private int paymodeId;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "cancel_remark")
	private String cancelRemark;

	@Column(name = "receipt_no")
	private String receiptNo;

	@Column(name = "invoice_id", nullable = false)
	private Long invoiceId;

	@Column(name = "is_wallet_payment", nullable = false)
	private char isWalletPayment;

	@Column(name = "is_sub_payment", nullable = false)
	private char isSubscriptionPayment;

	@Column(name = "ref_id", nullable = true)
	private Long refernceId;

	@Column(name = "payment_source")
	private String paymentSource;

	@Column(name = "conversion_charges")
	private String conversionCharges;

	@OneToOne
	@JoinColumn(name = "currency_Id")
	private ApplicationCurrency currencyId;

	public Payment() {
	}

	public Payment(final Long clientId, final Long paymentId, final Long externalId, final BigDecimal amountPaid,
			final Long statmentId, final LocalDate paymentDate, final String remark, final Long paymodeCode,
			final String transId, final String receiptNo, final Long invoiceId, boolean isWalletPayment,
			boolean isSubscriptionPayment, final String paymentSource) {

		this.clientId = clientId;
		this.statementId = statmentId;
		this.amountPaid = amountPaid;
		this.paymentDate = paymentDate.toDate();
		this.remarks = remark;
		this.paymodeId = paymodeCode.intValue();
		this.transactionId = transId;
		this.receiptNo = receiptNo.isEmpty() ? null : receiptNo;
		this.invoiceId = invoiceId;
		this.isWalletPayment = isWalletPayment ? 'Y' : 'N';
		this.isSubscriptionPayment = isSubscriptionPayment ? 'Y' : 'N';
		this.paymentSource = paymentSource;

	}

	public Payment(final Long clientId, final Long paymentId, final Long externalId, final BigDecimal amountPaid,
			final Long statmentId, final LocalDate paymentDate, final String remark, final int paymodeId,
			final String transId, final String receiptNo, final Long invoiceId, char isWalletPayment,
			char isSubscriptionPayment, final Long referenceId) {

		this.clientId = clientId;
		this.statementId = statmentId;
		this.amountPaid = amountPaid.negate();
		this.paymentDate = paymentDate.toDate();
		this.remarks = remark;
		this.paymodeId = paymodeId;
		this.transactionId = transId;
		if (this.receiptNo != null)
			this.receiptNo = receiptNo + "_CP";
		this.invoiceId = invoiceId;
		this.isWalletPayment = isWalletPayment;
		this.isSubscriptionPayment = isSubscriptionPayment;
		this.refernceId = referenceId;

	}
	
	public Payment(final Long clientId, final Long paymentId, final Long externalId, final BigDecimal amountPaid,
			final Long statmentId, final LocalDate paymentDate, final String remark, final int paymodeId,
			final String transId, final String receiptNo, final Long invoiceId, char isWalletPayment,
			char isSubscriptionPayment, final Long referenceId,final ApplicationCurrency currencyId) {

		this.clientId = clientId;
		this.statementId = statmentId;
		this.amountPaid = amountPaid.negate();
		this.paymentDate = paymentDate.toDate();
		this.remarks = remark;
		this.paymodeId = paymodeId;
		this.transactionId = transId;
		if (this.receiptNo != null)
			this.receiptNo = receiptNo + "_CP";
		this.invoiceId = invoiceId;
		this.isWalletPayment = isWalletPayment;
		this.isSubscriptionPayment = isSubscriptionPayment;
		this.refernceId = referenceId;
		this.currencyId =currencyId;

	}

	public static Payment fromJson(final JsonCommand command, final Long clientid) {
		final LocalDate paymentDate = command.localDateValueOfParameterNamed("paymentDate");
		final Long paymentCode = command.longValueOfParameterNamed("paymentCode");

		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		final String remarks = command.stringValueOfParameterNamed("remarks");
		final String txtid = command.stringValueOfParameterNamed("txn_id");
		final String receiptNo = command.stringValueOfParameterNamed("receiptNo");
		/* String concatReciept="R-"+receiptNo; */
		final Long invoiceId = command.longValueOfParameterNamed("invoiceId");
		final boolean isWalletPayment = command.booleanPrimitiveValueOfParameterNamed("isWalletPayment");
		final boolean isSubscriptionPayment = command.booleanPrimitiveValueOfParameterNamed("isSubscriptionPayment");
		final String paymentSource = command.stringValueOfParameterNamed("paymentSource");
		return new Payment(clientid, null, null, amountPaid, null, paymentDate, remarks, paymentCode, txtid, receiptNo,
				invoiceId, isWalletPayment, isSubscriptionPayment, paymentSource);

	}

	public Long getClientId() {
		return clientId;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public Long getStatementId() {
		return statementId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public int getPaymodeCode() {
		return paymodeId;
	}

	public String getPaymentSource() {
		return paymentSource;
	}

	public void updateBillId(final Long billId) {
		this.statementId = billId;

	}

	public void cancelPayment(final JsonCommand command) {

		final String cancelRemarks = command.stringValueOfParameterNamed("cancelRemark");
		this.cancelRemark = cancelRemarks;
		this.deleted = true;

	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public int getPaymodeId() {
		return paymodeId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getCancelRemark() {
		return cancelRemark;
	}

	public char isWalletPayment() {
		return isWalletPayment;
	}

	public void setInvoiceId(final Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public char getIsSubscriptionPayment() {
		return isSubscriptionPayment;
	}

	public Long getRefernceId() {
		return refernceId;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public ApplicationCurrency getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(ApplicationCurrency currencyId) {
		this.currencyId = currencyId;
	}

	public String getConversionCharges() {
		return conversionCharges;
	}

	public void setConversionCharges(String conversionCharges) {
		this.conversionCharges = conversionCharges;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	
	
}
