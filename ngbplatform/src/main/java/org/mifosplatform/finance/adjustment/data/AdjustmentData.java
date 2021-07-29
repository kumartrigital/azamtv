package org.mifosplatform.finance.adjustment.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

public class AdjustmentData {
	
	


	private Long id;
	private Long clientId;
	private LocalDate adjustmentDate;
	private String adjustmentCode;
	private String adjustmentType;
	private BigDecimal amountPaid;
	private Long billId;
	private Long externalId;
	private String remarks;
	
	
	
	private Long clientPoId;
	
	private Long billPoId;
	
	private boolean withtax;
	
	public AdjustmentData(final Long id, final Long clientId,	final LocalDate adjustmentDate, final String adjustmentCode,
			final BigDecimal amountPaid, final Long billId, final Long externalId, final String remarks) {
		this.id = id;
		this.setClientId(clientId);
		this.setAdjustmentDate(adjustmentDate);
		this.adjustmentCode = adjustmentCode;
		this.setAmountPaid(amountPaid);
		this.setBillId(billId);
		this.setExternalId(externalId);
		this.setRemarks(remarks);
	}

	public AdjustmentData(final Long id, final String adjustmentCode) {

		this.id=id;
		this.adjustmentCode=adjustmentCode;
		
	}
	
	
	public static AdjustmentData instance(final Long id, final Long clientId, final LocalDate adjustmentDate, final String adjustmentCode,
			final BigDecimal amountPaid, final Long billId, final Long externalId, final String remarks){
		
		return new AdjustmentData(id,clientId,adjustmentDate,adjustmentCode,amountPaid,billId,externalId,remarks);
		
	}
	
	
	public AdjustmentData(final Long clientPoId, final BigDecimal amountPaid,final String remarks,final String adjustmentType) {

		this.clientPoId=clientPoId;
		this.amountPaid=amountPaid;
		this.remarks=remarks;
		this.adjustmentType=adjustmentType;
		
		
	}
	public AdjustmentData(final Long billPoId,final Long clientPoId,final BigDecimal amountPaid,final String remarks,final boolean withtax) {

		this.billPoId=billPoId;
		this.clientPoId=clientPoId;
		this.amountPaid=amountPaid;
		this.remarks=remarks;
		this.withtax=withtax;
		
		
	}
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getAdjustmentCode() {
		return adjustmentCode;
	}

	public void setAdjustmentCode(final String adjustmentCode) {
		this.adjustmentCode = adjustmentCode;
	}

	public LocalDate getAdjustmentDate() {
		return adjustmentDate;
	}

	public void setAdjustmentDate(final LocalDate adjustmentDate) {
		this.adjustmentDate = adjustmentDate;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(final String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(final BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(final Long billId) {
		this.billId = billId;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(final Long externalId) {
		this.externalId = externalId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}
	
	public Long getClientPoId() {
		return clientPoId;
	}

	public void setClientPoId(Long clientPoId) {
		this.clientPoId = clientPoId;
	}


	public static AdjustmentData fromJsonToAdjustmentsCelcom(JsonCommand command) {
		
		final Long clientPoId = command.longValueOfParameterNamed("clientPoId");
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amount_paid");
		final String remarks =command.stringValueOfParameterNamed("Remarks");
		final String adjustmentType=command.stringValueOfParameterName("adjustment_type");
		
		
		
	    return new AdjustmentData(clientPoId,amountPaid,remarks,adjustmentType);
	}

	public String celcomRequestInputForADJUSTMENTSCelcom(String userName) {
		
		  String flag="2";
	        if(this.adjustmentType.equalsIgnoreCase("DEBIT")){
	            flag="Debit Not Implemented";
	        }
	        if(this.adjustmentType.equalsIgnoreCase("CREDIT")){
	        	this.amountPaid = this.amountPaid.multiply(new BigDecimal(-1));
	        }
		
		StringBuilder sb = new StringBuilder("<COB_OP_AR_ACCOUNT_ADJUSTMENT_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 3</POID>");
		/*<POID>0.0.0.1 /account 367556 13</POID>*/
		sb.append("<AMOUNT>"+this.amountPaid+"</AMOUNT>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<STR_VERSION>8</STR_VERSION>");
		sb.append("<STRING_ID>3</STRING_ID>");
		sb.append("<DESCR>"+this.remarks+"</DESCR>");
		sb.append("<BAL_GRP_OBJ>0.0.0.1 /balance_group 367652 0</BAL_GRP_OBJ>");
		/*sb.append("<FLAGS>2</FLAGS>");*/
		sb.append("<FLAGS>"+flag+"</FLAGS>");
		sb.append("</COB_OP_AR_ACCOUNT_ADJUSTMENT_inputFlist>");
		
		return sb.toString();
		
	}
public static AdjustmentData fromJsonToBillAdjustmentsCelcom(JsonCommand command) {
		
		final Long billPoId = command.longValueOfParameterNamed("billpoId");
		final Long clientPoId = command.longValueOfParameterNamed("clientPoId");
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amount_paid");
		final String remarks =command.stringValueOfParameterNamed("Remarks");
		final boolean withtax=command.booleanPrimitiveValueOfParameterNamed("withtax");
		
		
		
	    return new AdjustmentData(billPoId,clientPoId,amountPaid,remarks,withtax);
	}
	
	
	
	public String celcomRequestInputForBILLADJUSTMENTSCelcom(String userName) {
		String flag=null;
        if(this.withtax){
            flag="2";
        }else{
        	flag="1";
        }
	
		StringBuilder sb = new StringBuilder("<COB_OP_AR_BILL_ADJUSTMENT_inputFlist>");
		sb.append("<POID>0.0.0.1 /bill "+this.billPoId+"</POID>");
		sb.append("<AMOUNT>-2</AMOUNT>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<STR_VERSION>8</STR_VERSION>");
		sb.append("<STRING_ID>3</STRING_ID>");
		sb.append("<DESCR>"+this.remarks+"</DESCR>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.clientPoId+"</ACCOUNT_OBJ>");
		/*sb.append("<FLAGS>2</FLAGS>");*/
		sb.append("<FLAGS>"+flag+"</FLAGS>");
		sb.append("</COB_OP_AR_BILL_ADJUSTMENT_inputFlist>");
		
		return sb.toString();
		
	}

}
