package org.mifosplatform.organisation.partneragreement.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.order.data.OrderData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class AgreementData {
	
	private Long id;
	private String agreementStatus;
	private Long officeId;
	private LocalDate startDate;
	private LocalDate endDate;
	private String shareType;
	private BigDecimal shareAmount;
	private String source;
	private Long planId;
	private Long contractPeriod;
	private String billingFrequency;
	private Long detailId;
	private Long partnerId;
	private Collection<MCodeData> shareTypes;
	private Collection<MCodeData> sourceData;
	private List<EnumOptionData> statusData;
	private Collection<MCodeData> agreementTypes;
	private String officeType;
	private Long sourceId;
	private Long chargeId;
	private BigDecimal commisionAmount;
	private String planCode;
	private String poId;
	private String planPoId;
	private String dealPoId;
	private String settlementPoId;
	private String planDescription;
	private String purchaseProductPoId;
	private String packageId;
	private Long   clientId;
	private Long   orderId;


	public AgreementData(Collection<MCodeData> shareTypes,Collection<MCodeData> sourceData, 
			 Collection<MCodeData> agreementTypes) {

		this.shareTypes = shareTypes;
		this.sourceData = sourceData;
		this.agreementTypes = agreementTypes;

	}


	public AgreementData(Long id,String agreementStatus, Long officeId, LocalDate startDate,LocalDate endDate,  
			String shareType, BigDecimal shareAmount,String source, Long detailId,Long planId,Long contractPeriod,String billingFrequency) {
		
		this.id=id;
		this.agreementStatus = agreementStatus;
		this.officeId = officeId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.shareType = shareType;
		this.shareAmount = shareAmount;
		this.source = source;
		this.detailId = detailId;
		this.planId = planId;
		this.contractPeriod = contractPeriod;
		this.billingFrequency = billingFrequency;
		
	}

	public AgreementData(Long orderId, String poId, Long officeId,
			LocalDate startDate, LocalDate endDate,String planCode,String planPoId,String dealPoId,String planDescription,String settlementPoId,String packageId) {
		
		this.orderId=orderId;
		this.poId = poId;
		this.officeId = officeId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.planCode = planCode;
		this.planPoId = planPoId;
		this.dealPoId = dealPoId;
		this.planDescription = planDescription;
		this.settlementPoId = settlementPoId;
		this.packageId = packageId;
		
	}


	public AgreementData(Long officeId, String officeType, Long agreementId) {
		this.officeId = officeId;
		this.officeType = officeType;
		this.id = agreementId;
	}


	public AgreementData(Long chargeId, Long officeId, LocalDate invoiceDate,
			Long source, BigDecimal shareAmount, String shareType,
			String commisionSource, BigDecimal commisionAmount,Long planId,Long contractPeriod,String billingFrequency) {
		
		this.chargeId=chargeId;
		this.officeId=officeId;
		this.startDate = invoiceDate;
		this.sourceId = source ;
		this.shareAmount=shareAmount;
		this.shareType = shareType;
		this.source= commisionSource;
		this.commisionAmount=commisionAmount;
		this.planId=planId;
		this.contractPeriod = contractPeriod;
		this.billingFrequency= billingFrequency;
	}


	public AgreementData(String poId, String planPoId, String dealPoId,String settlementPoId,String packageId) {
	    
		this.poId = poId;
		this.planPoId = planPoId;
		this.dealPoId = dealPoId;
		this.settlementPoId = settlementPoId;
		this.packageId = packageId;
		
	}


	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public String getAgreementStatus() {
		return agreementStatus;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public String getShareType() {
		return shareType;
	}

	public BigDecimal getShareAmount() {
		return shareAmount;
	}

	public String getSource() {
		return source;
	}
	
	public Long getPlanId() {
		return planId;
	}

	public Long getContractPeriod() {
		return contractPeriod;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public Long getDetailId() {
		return detailId;
	}

	public Long getPartnerId() {
		return partnerId;
	}

	public String getOfficeType() {
		return officeType;
	}

	public Collection<MCodeData> getShareTypes() {
		return shareTypes;
	}

	public Collection<MCodeData> getSourceData() {
		return sourceData;
	}

	public List<EnumOptionData> getStatusData() {
		return statusData;
	}

	public Collection<MCodeData> getAgreementTypes() {
		return agreementTypes;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public Long getChargeId() {
		return chargeId;
	}

	public BigDecimal getCommisionAmount() {
		return commisionAmount;
	}

	
	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}
	
	public String getPlanPoId() {
		return planPoId;
	}

	public void setPlanPoId(String planPoId) {
		this.planPoId = planPoId;
	}
	
	public String getDealPoId() {
		return dealPoId;
	}

	public void setDealPoId(String dealPoId) {
		this.dealPoId = dealPoId;
	}
	
	public String getSettlementPoId() {
		return settlementPoId;
	}

	public void setSettlementPoId(String settlementPoId) {
		this.settlementPoId = settlementPoId;
	}
	
	public String getPlanDescription() {
		return planDescription;
	}

	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}
	
	public String getPurchaseProductPoId() {
		return purchaseProductPoId;
	}

	public void setPurchaseProductPoId(String purchaseProductPoId) {
		this.purchaseProductPoId = purchaseProductPoId;
	}
	
	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	
	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	/*public List<AgreementData> fromJsonToCreateAgreement(JsonCommand command) {
		
		String poId=command.stringValueOfParameterName("poId");
		String planPoId=command.stringValueOfParameterName("planPoId");
		String dealPoId=command.stringValueOfParameterName("dealPoId");
		String settlementPoId=command.stringValueOfParameterName("settlementPoId");
		//String packageId=command.stringValueOfParameterName("packageId");
	
	    return new AgreementData(poId,planPoId,dealPoId,settlementPoId,null);
	    
	    
	    AgreementData agreementData= null;
		List<AgreementData> plans=new ArrayList<AgreementData>();
		final JsonArray multiplePlans = command.arrayOfParameterNamed("plans").getAsJsonArray();
		for(JsonElement planElement : multiplePlans){
			String poId=this.fromApiJsonHelper.extractStringNamed("poId", planElement);
			String planPoId=this.fromApiJsonHelper.extractStringNamed("planPoId", planElement);
			String dealPoId=this.fromApiJsonHelper.extractStringNamed("dealPoId", planElement);
			String settlementPoId=this.fromApiJsonHelper.extractStringNamed("settlementPoId", planElement);
			agreementData=new AgreementData(poId, planPoId, dealPoId, settlementPoId,null);
			plans.add(agreementData);
		}
		return plans;
	
	}*/
	
	public static String celcomRequestInputForCreateAgreement(List<AgreementData> datas,String userName,String order) {	

		//int packageId=10000+new Random().nextInt(89999);
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_ADD_PARTNER_AGREEMENT_inputFlist>");
		for(AgreementData data : datas){
			sb.append("<POID>0.0.0.1 /account "+data.getPoId()+" 0</POID>");		
			sb.append("<SERVICE_OBJ>0.0.0.1 /service/settlement/prepaid "+data.getSettlementPoId()+" 0</SERVICE_OBJ>");		
			sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+data.getPoId()+" 0</ACCOUNT_OBJ>");		
			sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");		
			sb.append("<PLAN_LIST_CODE>");			
			sb.append("<PLAN elem=\"0\">");		
			sb.append("<PLAN_OBJ>0.0.0.1 /plan "+data.getPlanPoId()+" 2</PLAN_OBJ>");	
	        sb.append("<PACKAGE_ID>"+order+"</PACKAGE_ID>");	
		    sb.append("<DEALS elem=\"0\">");
		    sb.append("<DEAL_OBJ>0.0.0.1 /deal "+data.getDealPoId()+" 1</DEAL_OBJ>");
			sb.append("</DEALS>");	
			sb.append("</PLAN>");	
		}
		sb.append("</PLAN_LIST_CODE>");			
		sb.append("</COB_OP_CUST_ADD_PARTNER_AGREEMENT_inputFlist>");
		
		return sb.toString();
	}

public static AgreementData fromJsonToDeleteAgreement(JsonCommand command) {
		
		String poId=command.stringValueOfParameterName("poId");
		String planPoId=command.stringValueOfParameterName("planPoId");
		String dealPoId=command.stringValueOfParameterName("dealPoId");
		String settlementPoId=command.stringValueOfParameterName("settlementPoId");
		String packageId=command.stringValueOfParameterName("orderNo");
	
	    return new AgreementData(poId,planPoId,dealPoId,settlementPoId,packageId);
	
	}
	

public  String celcomRequestInputForDeleteAgreement() {	

	//int packageId=10000+new Random().nextInt(89999);
	StringBuilder sb = new StringBuilder("<COB_OP_CUST_CANCEL_PARTNER_AGREEMENT_inputFlist>");
	sb.append("<POID>0.0.0.1 /account "+this.poId+" 0</POID>");		
	sb.append("<SERVICE_OBJ>0.0.0.1 /service/settlement/prepaid "+this.settlementPoId+" 0</SERVICE_OBJ>");		
	sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.poId+" 0</ACCOUNT_OBJ>");
	sb.append("<FLAGS>0</FLAGS>");
	sb.append("<PROGRAM_NAME>COB_OP_CUST_CANCEL_PARTNER_AGREEMENT</PROGRAM_NAME>");		
	sb.append("<PLAN_LIST_CODE>");			
	sb.append("<PLAN elem=\"0\">");		
	sb.append("<PLAN_OBJ>0.0.0.1 /plan "+this.planPoId+" 2</PLAN_OBJ>");	
    sb.append("<DEALS elem=\"0\">");
    sb.append("<PACKAGE_ID>"+this.packageId+"</PACKAGE_ID>");
    sb.append("<DEAL_OBJ>0.0.0.1 /deal "+this.dealPoId+" 1</DEAL_OBJ>");	
	sb.append("</DEALS>");	
	sb.append("</PLAN>");	
	sb.append("</PLAN_LIST_CODE>");			
	sb.append("</COB_OP_CUST_CANCEL_PARTNER_AGREEMENT_inputFlist>");
	
	return sb.toString();
}
	
}
