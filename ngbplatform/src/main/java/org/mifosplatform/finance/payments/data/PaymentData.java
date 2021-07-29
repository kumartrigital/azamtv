package org.mifosplatform.finance.payments.data;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

public class PaymentData {
	
    private Collection<McodeData> data;
	private LocalDate paymentDate;
	private String clientName;
	private BigDecimal amountPaid;
	private String payMode;
	private Boolean isDeleted;
	private Long billNumber;
	private String receiptNo;
	private Long id;
	private BigDecimal availAmount;
	private Date transactionDate;
	private BigDecimal debitAmount;
	private List<PaymentData> depositDatas;
	
	private Long clientPoId;
	private String paymentCode;
	private String bankName;
	private String chequeNo;
	private String branchName;
	private String paymentType;
	private String currency;
	
	private String cancelRemark;

	


	public PaymentData(final Collection<McodeData> data,final List<PaymentData> depositDatas){
		this.data= data;
		this.depositDatas = depositDatas;
	}
	
	
	public PaymentData(final String clientName, final String payMode,final LocalDate paymentDate, final BigDecimal amountPaid, final Boolean isDeleted, final Long billNumber, final String receiptNumber,final String currency) {
		  this.clientName = clientName;
		  this.payMode = payMode;
		  this.paymentDate = paymentDate;
		  this.amountPaid = amountPaid;
		  this.isDeleted = isDeleted;
		  this.billNumber = billNumber;
		  this.receiptNo = receiptNumber;
		  this.currency=currency;
		 }


	public PaymentData(final Long id, final LocalDate paymentdate, final BigDecimal amount,final String recieptNo, final BigDecimal availAmount,final String currency) {
	
		this.id=id;
		this.paymentDate=paymentdate;
		this.amountPaid=amount;
		this.receiptNo=recieptNo;
		this.availAmount=availAmount;
		this.currency=currency;
	}


	public PaymentData(Long id, Date transactionDate, BigDecimal debitAmount) {
		
		this.id = id;
		this.transactionDate = transactionDate;
		this.debitAmount = debitAmount;
	}
    
	public PaymentData(final LocalDate paymentdate,final String paymentCode,final BigDecimal amountPaid,
			final String recieptNo,final Long clientPoId,final String bankName,final String chequeNo,
			final String branchName,final String paymentType,String currency) {
	
		this.paymentDate=paymentdate;
		this.paymentCode=paymentCode;
		this.amountPaid=amountPaid;
		this.receiptNo=recieptNo;
		this.clientPoId=clientPoId;
		this.bankName=bankName;
		this.chequeNo=chequeNo;
		this.branchName=branchName;
		this.paymentType = paymentType;
		this.currency=currency;
	}
	
	
    public PaymentData(Long clientPoId, String receiptNo, String cancelRemark) {
    	
    	this.clientPoId=clientPoId;
    	this.receiptNo=receiptNo;
    	this.cancelRemark=cancelRemark;
    	
	}
	
	
	
	

	public PaymentData() {
		
	}

	public PaymentData(long id2, long clientPoid2) {
		this.id=id2;
		this.clientPoId=clientPoid2;
	}


	public Collection<McodeData> getData() {
		return data;
	}


	public LocalDate getPaymentDate() {
		return paymentDate;
	}


	public String getClientName() {
		return clientName;
	}


	public BigDecimal getAmountPaid() {
		return amountPaid;
	}


	public String getPayMode() {
		return payMode;
	}


	public Boolean getIsDeleted() {
		return isDeleted;
	}


	public Long getBillNumber() {
		return billNumber;
	}


	public String getReceiptNo() {
		return receiptNo;
	}

	public String getCurrency() {
		return currency;
	}

	public Long getId() {
		return id;
	}


	public BigDecimal getAvailAmount() {
		return availAmount;
	}


	public void setAvailAmount(BigDecimal availAmount) {
		this.availAmount = availAmount;
	}

	public void setData(Collection<McodeData> payData) {
		this.data = payData;

	}
	
	public Long getClientPoId() {
		return clientPoId;
	}


	public void setClientPoId(Long clientPoId) {
		this.clientPoId = clientPoId;
	}
	
	public String getPaymentCode() {
		return paymentCode;
	}


	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	

	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getChequeNo() {
		return chequeNo;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getBranchName() {
		return branchName;
	}


	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public static PaymentData fromJsonToPaymentsCelcom(JsonCommand command) {
		final LocalDate paymentDate = command.localDateValueOfParameterNamed("paymentDate");
		final String paymentCode = command.stringValueOfParameterNamed("paymentCode");
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		final Long clientPoId = command.longValueOfParameterNamed("clientPoId");
		final String bankName = command.stringValueOfParameterNamed("bankName");
		final String chequeNo = command.stringValueOfParameterNamed("chequeNo");
		final String branchName = command.stringValueOfParameterNamed("branchName");
		final String paymentType = command.stringValueOfParameterNamed("paymentType");
		final String currency = command.stringValueOfParameterNamed("currency");
		return new PaymentData(paymentDate, paymentCode, amountPaid, receiptNo, 
				clientPoId,bankName,chequeNo,branchName,paymentType,currency);
	}


	public String celcomRequestInputForPaymentsCelcome(String userName) {
		
		StringBuilder sb = new StringBuilder("<COB_OP_PYMT_COLLECT_PAYMENT_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 8</POID>");
		sb.append("<COB_FLD_SERVICE_TYPE>0</COB_FLD_SERVICE_TYPE>");
		/*sb.append("<COB_FLD_SERVICE_TYPE>1</COB_FLD_SERVICE_TYPE>");*/
		sb.append("<COB_FLD_PYMT_CHANNEL >1</COB_FLD_PYMT_CHANNEL >");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<CHARGES elem=\"0\">");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.clientPoId+" 8</ACCOUNT_OBJ>");
		sb.append("<AMOUNT>"+this.amountPaid+"</AMOUNT>");
		sb.append("<COMMAND>0</COMMAND>");
		sb.append("<PAYMENT>");
		sb.append("<INHERITED_INFO>");
		if("cash".equalsIgnoreCase(this.paymentType)){
			sb.append("<CASH_INFO elem=\"0\">");
			sb.append("<EFFECTIVE_T>"+System.currentTimeMillis()/1000+"</EFFECTIVE_T>");
			sb.append("<RECEIPT_NO>Cash prepaid</RECEIPT_NO>");
			sb.append("</CASH_INFO>");
			sb.append("</INHERITED_INFO>");
			sb.append("</PAYMENT>");
			sb.append("<PAY_TYPE>10011</PAY_TYPE>");
			sb.append("</CHARGES>");
			sb.append("<DESCR>Prepaid Recharge </DESCR>");
		}
		else{
			sb.append("<CHECK_INFO elem=\"0\">");
			sb.append("<EFFECTIVE_T>"+System.currentTimeMillis()/1000+"</EFFECTIVE_T>");
			sb.append("<BANK_CODE>"+this.bankName+"</BANK_CODE>");
			sb.append("<CHECK_NO>"+this.chequeNo+"</CHECK_NO>");
			sb.append("<BANK_ACCOUNT_NO>"+this.branchName+"</BANK_ACCOUNT_NO>");
			sb.append("</CHECK_INFO>");
			sb.append("</INHERITED_INFO>");
			sb.append("</PAYMENT>");
			sb.append("<PAY_TYPE>10012</PAY_TYPE>");
			sb.append("</CHARGES>");
			sb.append("<DESCR>check payent </DESCR>");
		}
		
		sb.append("</COB_OP_PYMT_COLLECT_PAYMENT_inputFlist>");
		
		return sb.toString();
	}


	public static PaymentData fromJsonToCancelPaymentsCelcom(JsonCommand command) {
		final Long clientPoId = command.longValueOfParameterNamed("clientPoId");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		final String cancelRemark=command.stringValueOfParameterNamed("cancelRemark");
		return new PaymentData(clientPoId, receiptNo, cancelRemark);
				
	} 
	
	
	
	public String celcomRequestInputForCancelPayment(String userName) {
		
		StringBuilder sb = new StringBuilder("<COB_OP_PYMT_PAYMENT_REVERSAL_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+"</POID>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<RECEIPT_NO>"+this.receiptNo+"</RECEIPT_NO>");
		sb.append("<REASON_CODE>"+this.cancelRemark+"</REASON_CODE>");
		sb.append("</COB_OP_PYMT_PAYMENT_REVERSAL_inputFlist>");
		return sb.toString();
	}


	public Long getclientPoid() {
		return clientPoId;
	}
	
}
