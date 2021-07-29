package org.mifosplatform.organisation.officepayments.data;

import java.math.BigDecimal;
import java.util.Date;

import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.officepayments.domain.OfficePayments;

public class OfficePaymentData {

	
	private String officePoId;
	private String paymentCode;
	private String bankName;
	private String chequeNo;
	private String branchName;
	private String paymentType;
	private String receiptNo;
	private Date paymentDate;
	private BigDecimal amountPaid;
	private boolean isWallet;
	
	
	private String cancelRemark;
	private Long officePoid;
	
	
	public boolean isWallet() {
		return isWallet;
	}


	public void setWallet(boolean isWallet) {
		this.isWallet = isWallet;
	}


	public String getOfficePoId() {
		return officePoId;
	}


	public void setOfficePoId(String officePoId) {
		this.officePoId = officePoId;
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


	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}


	public String getBranchName() {
		return branchName;
	}


	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getReceiptNo() {
		return receiptNo;
	}


	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}


	public Date getPaymentDate() {
		return paymentDate;
	}


	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}


	public BigDecimal getAmountPaid() {
		return amountPaid;
	}


	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}


	public OfficePaymentData(final Date paymentdate,final String paymentCode,final BigDecimal amountPaid,
			final String recieptNo,final String officePoId,final String bankName,final String chequeNo,
			final String branchName,final String paymentType) {
	
		this.paymentDate=paymentdate;
		this.paymentCode=paymentCode;
		this.amountPaid=amountPaid;
		this.receiptNo=recieptNo;
		this.officePoId=officePoId;
		this.bankName=bankName;
		this.chequeNo=chequeNo;
		this.branchName=branchName;
		this.paymentType = paymentType;
		
	}
	
	
	public static OfficePaymentData fromJsonToCelcomOfficePayment(JsonCommand command) {
		final Date paymentDate = command.DateValueOfParameterNamed("paymentDate");
		final String paymentCode = command.stringValueOfParameterNamed("paymentCode");
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		final String officePoId = command.stringValueOfParameterNamed("officePoId");
		final String bankName = command.stringValueOfParameterNamed("bankName");
		final String chequeNo = command.stringValueOfParameterNamed("chequeNo");
		final String branchName = command.stringValueOfParameterNamed("branchName");
		final String paymentType = command.stringValueOfParameterNamed("paymentType");
		
		return new OfficePaymentData(paymentDate, paymentCode, amountPaid, receiptNo, 
				officePoId,bankName,chequeNo,branchName,paymentType);
		
		
	}

	public String celcomRequestInputForOfficePaymentsCelcom(String userName) {

		String serviceType="3";
		if(this.isWallet){
			serviceType="2";
		}
		StringBuilder sb = new StringBuilder("<COB_OP_PYMT_COLLECT_PAYMENT_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.officePoId+" 0</POID>");
		sb.append("<COB_FLD_SERVICE_TYPE>"+serviceType+"</COB_FLD_SERVICE_TYPE>");
		/*sb.append("<COB_FLD_SERVICE_TYPE>1</COB_FLD_SERVICE_TYPE>");*/
		sb.append("<COB_FLD_PYMT_CHANNEL >1</COB_FLD_PYMT_CHANNEL >");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<CHARGES elem=\"0\">");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.officePoId+" 0</ACCOUNT_OBJ>");
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
	 public OfficePaymentData(Long officePoid, String receiptNo, String cancelRemark) {
	    	this.officePoid=officePoid;
	    	this.receiptNo=receiptNo;
	    	this.cancelRemark=cancelRemark;

	 }
	public static OfficePaymentData fromJsonToCancelPaymentsOfficeCelcom(JsonCommand command) {
		final Long officePoid = command.longValueOfParameterNamed("officePoid");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		final String cancelRemark=command.stringValueOfParameterNamed("cancelRemark");
		return new OfficePaymentData(officePoid, receiptNo, cancelRemark);
	}

	public String celcomRequestInputForOfficeCancelPayment(String userName) {
		StringBuilder sb = new StringBuilder("<COB_OP_PYMT_PAYMENT_REVERSAL_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.officePoid+"</POID>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<RECEIPT_NO>"+this.receiptNo+"</RECEIPT_NO>");
		sb.append("<REASON_CODE>"+this.cancelRemark+"</REASON_CODE>");
		sb.append("</COB_OP_PYMT_PAYMENT_REVERSAL_inputFlist>");
		return sb.toString();
	}
	
	
}
