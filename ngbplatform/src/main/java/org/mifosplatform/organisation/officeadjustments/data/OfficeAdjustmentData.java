package org.mifosplatform.organisation.officeadjustments.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

public class OfficeAdjustmentData {

	
	private Long id;
	private Long officeId;
	private LocalDate adjustmentDate;
	private String adjustmentCode;
	private String adjustmentType;
	private BigDecimal amountPaid;
	private Long billId;
	private Long externalId;
	private String remarks;
	
	private Long officePoId;
	
	
	public OfficeAdjustmentData(final Long officePoId, final BigDecimal amountPaid,final String remarks) {

		this.officePoId=officePoId;
		this.amountPaid=amountPaid;
		this.remarks=remarks;
		
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public LocalDate getAdjustmentDate() {
		return adjustmentDate;
	}

	public void setAdjustmentDate(LocalDate adjustmentDate) {
		this.adjustmentDate = adjustmentDate;
	}

	public String getAdjustmentCode() {
		return adjustmentCode;
	}

	public void setAdjustmentCode(String adjustmentCode) {
		this.adjustmentCode = adjustmentCode;
	}

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getOfficePoId() {
		return officePoId;
	}

	public void setOfficePoId(Long officePoId) {
		this.officePoId = officePoId;
	}


	public static OfficeAdjustmentData fromJsonToOfficeAdjustmentsCelcom(JsonCommand command) {
		
		final Long officePoId = command.longValueOfParameterNamed("officePoId");
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		final String remarks =command.stringValueOfParameterNamed("remarks");
		
	    return new OfficeAdjustmentData(officePoId,amountPaid,remarks);
	}


	public String celcomRequestInputForOfficeAdjustmentsCelcom(String userName) {
		
		StringBuilder sb = new StringBuilder("<COB_OP_AR_ACCOUNT_ADJUSTMENT_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.officePoId+" 3</POID>");
		/*<POID>0.0.0.1 /account 367556 13</POID>*/
		sb.append("<AMOUNT>"+this.amountPaid+"</AMOUNT>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<STR_VERSION>8</STR_VERSION>");
		sb.append("<STRING_ID>3</STRING_ID>");
		sb.append("<DESCR>"+this.remarks+"</DESCR>");
		sb.append("<BAL_GRP_OBJ>0.0.0.1 /balance_group 367652 0</BAL_GRP_OBJ>");
		sb.append("<FLAGS>2</FLAGS>");
		sb.append("</COB_OP_AR_ACCOUNT_ADJUSTMENT_inputFlist>");
		
		return sb.toString();
	}

	
	
	
	
	
	
	
}
