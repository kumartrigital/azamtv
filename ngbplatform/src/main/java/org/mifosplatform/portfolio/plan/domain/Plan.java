package org.mifosplatform.portfolio.plan.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.contract.domain.Contract;


@Entity
@Table(name = "b_plan_master", uniqueConstraints = {@UniqueConstraint(name = "uplan_code_key", columnNames = { "plan_code" }),@UniqueConstraint(name = "plan_description", columnNames = { "plan_description" })})
public class Plan{


	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name = "plan_code", length = 65536)
	private String planCode;

	@Column(name = "plan_description")
	private String description;

	@Column(name = "plan_status")
	private Long status;
	
	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "bill_rule")
	private Long billRule;

	@Column(name = "is_deleted", nullable = false)
	private char deleted='N';
	
	@Column(name = "is_prepaid", nullable = false)
	private char isPrepaid='N';
	
	@Column(name = "allow_topup", nullable = false)
	private char allowTopup='N';
	
	@Column(name = "is_hw_req", nullable = false)
	private char isHwReq;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "plan", orphanRemoval = true)
	private Set<PlanDetails> planDetails = new HashSet<PlanDetails>();
	
	/*//@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "plan", orphanRemoval = true)
	private Set<PlanQualifier> planQualifier = new HashSet<PlanQualifier>();*/
	
	@Column(name = "provision_sys")
	private String provisionSystem;
	
	@Column(name = "plan_type")
	private Long planType;
	
	@Column(name = "currencyId")
	private String currencyId;
	
	@Column(name = "plan_poid")
	private Long planPoid;
	
	@Column(name = "is_advance", nullable = false)
	private char isAdvance='N';

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duration",nullable = true)	
    private Contract duration;

	public Plan() {
		// TODO Auto-generated constructor stub
	}

	public Plan(final String code, final String description,final LocalDate start_date, final LocalDate endDate,
			final Long bill_rule, final Long status,final List<PlanDetails> details,final String provisioingSystem,
			final boolean isPrepaid,final boolean allowTopup,final boolean isHwReq, final Long planType,final String currencyId,final boolean isAdvance) {
			
				this.planCode = code;
				this.description = description;
				if (endDate != null)
				  this.endDate = endDate.toDate();
				this.startDate = start_date.toDate();
				this.status = status;
				this.billRule = bill_rule;
				this.provisionSystem=provisioingSystem;
				this.isPrepaid=isPrepaid?'Y':'N';
				this.allowTopup=allowTopup?'Y':'N';
				this.isHwReq=isHwReq?'Y':'N';
				this.planType=planType;
				this.currencyId=currencyId;
				this.isAdvance = isAdvance?'Y':'N';
				
		}
	public Plan(final String code, final String description,final Date start_date, final Date endDate,
			final Long bill_rule, final Long status,final Set<PlanDetails> details,final String provisioingSystem,
			final boolean isPrepaid,final boolean allowTopup,final boolean isHwReq, final Long planType,final String currencyId,
			final Long planPoid) {
			
				this.planCode = code;
				this.description = description;
				this.endDate = endDate;
				this.startDate = start_date;
				this.status = status;
				this.billRule = bill_rule;
				this.provisionSystem=provisioingSystem;
				this.isPrepaid=isPrepaid?'Y':'N';
				this.allowTopup=allowTopup?'Y':'N';
				this.isHwReq=isHwReq?'Y':'N';
				this.planType=planType;
				this.currencyId=currencyId;
				this.planPoid=planPoid;
				this.planDetails=details;
		}
	
	public Plan(final String code, final String description,final LocalDate start_date, final LocalDate endDate,
			final Long bill_rule, final Long status,final Set<PlanDetails> details,final String provisioingSystem,
			final boolean isPrepaid,final boolean allowTopup,final boolean isHwReq, final Long planType,final String currencyId,
			final Long planPoid) {
			
				this.planCode = code;
				this.description = description;
				if (endDate != null)
					  this.endDate = endDate.toDate();
				if(start_date!=null)
					  this.startDate = start_date.toDate();
				else
					this.startDate= new Date();
				this.status = status;
				this.billRule = bill_rule;
				this.provisionSystem=provisioingSystem;
				this.isPrepaid=isPrepaid?'Y':'N';
				this.allowTopup=allowTopup?'Y':'N';
				this.isHwReq=isHwReq?'Y':'N';
				this.planType=planType;
				this.currencyId=currencyId;
				this.planPoid=planPoid;
		}
	
	
	public Set<PlanDetails> getDetails() {
			return planDetails;
		}

		

		public String getCode() {
		return planCode;
		}

		public String getDescription() {
			return description;
		}
		public Long getStatus() {
			return status;
		}
		
		public Long getId() {
			return id;
		}

		public String getPlanCode() {
			return planCode;
		}

		public Date getStartDate() {
			return startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public Long getBillRule() {
			return billRule;
		}

		public char isDeleted() {
			return deleted;
		}

		public void addProductDetails(final Set<PlanDetails> selectedProducts) {
			if(this.planDetails!=null){
				this.planDetails.clear();
			}
			for(PlanDetails planDetails:selectedProducts){
				planDetails.update(this);
				this.planDetails.add(planDetails);
			}
		}

		public char isHardwareReq() {
			return isHwReq;
		}

		public char getDeleted() {
			return deleted;
		}

		public String getProvisionSystem() {
			return provisionSystem;
		}

		public Long getPlanType() {
			return planType;
		}
		
		
		public String getCurrencyId() {
			return currencyId;
		}

		public char getIsHwReq() {
			return isHwReq;
		}

		public void setIsHwReq(char isHwReq) {
			this.isHwReq = isHwReq;
		}
		
		public Contract getDuration() {
			return duration;
		}

		public void setDuration(Contract duration) {
			this.duration = duration;
		}

		public void delete() {
				this.deleted = 'Y';
				this.planCode=this.planCode+"_"+this.getId()+"_DELETED";
				for(PlanDetails planDetails:this.planDetails){
					planDetails.delete();
				}
		}

	public Map<String, Object> update(final JsonCommand command) {
		 
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		final String firstnameParamName = "planCode";
			if (command.isChangeInStringParameterNamed(firstnameParamName, this.planCode)) {
				final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
	            actualChanges.put(firstnameParamName, newValue);
	            this.planCode = StringUtils.defaultIfEmpty(newValue, null);
	        }
	    final String descriptionParamName = "planDescription";
	        	if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
	        		final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
	        		actualChanges.put(firstnameParamName, newValue);
	        		this.description = StringUtils.defaultIfEmpty(newValue, null);
	        	}
	    final String provisioingSystem = "provisioingSystem";
	        if (command.isChangeInStringParameterNamed(provisioingSystem, this.provisionSystem)) {
	            final String newValue = command.stringValueOfParameterNamed(provisioingSystem);
	            actualChanges.put(provisioingSystem, newValue);
	            this.provisionSystem = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String productsParamName = "products";
	        if (command.isChangeInArrayParameterNamed(productsParamName, getProductsAsIdStringArray())) {
	            final String[] newValue = command.arrayValueOfParameterNamed(productsParamName);
	            actualChanges.put(productsParamName, newValue);
	        }
	        
	    final String startDateParamName = "startDate";
			if (command.isChangeInLocalDateParameterNamed(startDateParamName,
					new LocalDate(this.startDate))) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(startDateParamName);
				actualChanges.put(startDateParamName, newValue);
				this.startDate=newValue.toDate();
			}
			
		final String endDateParamName = "endDate";
				if (command.isChangeInLocalDateParameterNamed(endDateParamName,new LocalDate(this.endDate))) {
					final LocalDate newValue = command.localDateValueOfParameterNamed(endDateParamName);
					actualChanges.put(endDateParamName, newValue);
					if(newValue!=null)
						this.endDate=newValue.toDate();
				}
	    final String billRuleParamName = "billRule";
				if (command.isChangeInLongParameterNamed(billRuleParamName,this.billRule)) {
					final Long newValue = command.longValueOfParameterNamed(billRuleParamName);
					actualChanges.put(billRuleParamName, newValue);
					this.billRule=newValue;
				}
	    final String statusParamName = "status";
				if (command.isChangeInLongParameterNamed(statusParamName,this.status)) {
					final Long newValue = command.longValueOfParameterNamed(statusParamName);
					actualChanges.put(statusParamName, newValue);
					this.status=newValue;
				}
		final boolean isPrepaid=command.booleanPrimitiveValueOfParameterNamed("isPrepaid");
				final char isPrepaidParamName =isPrepaid?'Y':'N';
				this.isPrepaid=isPrepaidParamName;
						
	
		final boolean allowTopupParamName =command.booleanPrimitiveValueOfParameterNamed("allowTopup");
				this.allowTopup=allowTopupParamName?'Y':'N';
	          
	    
	    final boolean isHwReqParamName =command.booleanPrimitiveValueOfParameterNamed("isHwReq");
				this.isHwReq=isHwReqParamName?'Y':'N';
				
		final String planType = "planType";
        if (command.isChangeInLongParameterNamed(planType, this.planType)) {
            final Long newValue = command.longValueOfParameterNamed(planType);
            actualChanges.put(planType, newValue);
            this.planType = newValue;
        }
        
        if (command.isChangeInStringParameterNamed("currencyId", this.currencyId)) {
			final String newValue = command
					.stringValueOfParameterNamed("currencyId");
			actualChanges.put("currencyId", newValue);
			this.currencyId = StringUtils.defaultIfEmpty(newValue, null);

		}
       
        final String durationParamName = "duration";
        final String newValue = command.stringValueOfParameterNamed(durationParamName);
        if(newValue!=null) {
        	if(this.duration ==  null && !newValue.isEmpty()){
        		actualChanges.put(durationParamName, newValue);
            }else if(!newValue.isEmpty()){
            	if (command.isChangeInLongParameterNamed(durationParamName,this.duration.getId())) {
            			actualChanges.put(durationParamName, newValue);
        		}
        	    
            }
        }
        
        
        final boolean isAdvance=command.booleanPrimitiveValueOfParameterNamed("isAdvance");
		final char isAdvanceParamName =isAdvance?'Y':'N';
		this.isAdvance=isAdvanceParamName;
        /*final String durationParamName = "duration";
        final String newValue = command.stringValueOfParameterNamed(durationParamName);
        if(this.duration ==  null){
    		actualChanges.put(durationParamName, newValue);
        }else{
        	if (command.isChangeInLongParameterNamed(durationParamName,this.duration.getId())) {
    		actualChanges.put(durationParamName, newValue);
    		}
    	    
        }*/
        
		return actualChanges;
	    
	    
	}

	 private String[] getProductsAsIdStringArray() {
		 	
		 final List<String> roleIds = new ArrayList<>();
        	for (final PlanDetails details : this.planDetails) {
        		roleIds.add(details.getProductId().toString());
        	}
        	return roleIds.toArray(new String[roleIds.size()]);
	 }

	public static Plan fromJson(JsonCommand command) {
		 
		final String planCode = command.stringValueOfParameterNamed("planCode");
		final String planDescription = command.stringValueOfParameterNamed("planDescription");
		final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
		final LocalDate endDate = command.localDateValueOfParameterNamed("endDate");
		final Long status = command.longValueOfParameterNamed("status");
		final Long billRule = command.longValueOfParameterNamed("billRule");
		final String provisioingSystem=command.stringValueOfParameterNamed("provisioingSystem");
		final boolean isPrepaid=command.booleanPrimitiveValueOfParameterNamed("isPrepaid");
		final boolean allowTopup=command.booleanPrimitiveValueOfParameterNamed("allowTopup");
		final boolean isHwReq=command.booleanPrimitiveValueOfParameterNamed("isHwReq");
		final Long planType=command.longValueOfParameterNamed("planType");
		final String currencyId = command.stringValueOfParameterNamed("currencyId");
		final boolean isAdvance=command.booleanPrimitiveValueOfParameterNamed("isAdvance");
		return new Plan(planCode,planDescription,startDate,endDate,billRule,status,null,provisioingSystem,isPrepaid,allowTopup,isHwReq,planType,currencyId,isAdvance);
	}

	/**
	 * @return the isPrepaid
	 */
	public char isPrepaid() {
		return isPrepaid;
	}

	/**
	 * @return the allowTopup
	 */
	public char getAllowTopup() {
		return allowTopup;
	}

	public char getIsPrepaid() {
		return isPrepaid;
	}

	public void setIsPrepaid(char isPrepaid) {
		this.isPrepaid = isPrepaid;
	}

	public String toOBRMXML() {
		
		return null;
	}
	
	public Long getPlanPoid() {
		return planPoid;
	}

	public void setPlanPoid(Long planPoid) {
		this.planPoid = planPoid;
	}

	public Set<PlanDetails> getPlanDetails() {
		return planDetails;
	}

	public void setPlanDetails(Set<PlanDetails> planDetails) {
		this.planDetails = planDetails;
	}
	
	public char getIsAdvance() {
		return isAdvance;
	}

	public Set<PlanDetails> updatePlanDetails(Set<PlanDetails> planDetailsSet) {
		Set<PlanDetails>  planDetailsUpdateSet = null;
		for(PlanDetails planDetails:planDetailsSet){
			planDetails.update(planDetails);
			planDetailsSet.add(planDetails);
		}
		return planDetailsUpdateSet;
	}
	
	public void update(Plan plan) {
		
		this.planCode=plan.getCode();
		this.description=plan.getDescription();
		this.startDate=plan.getStartDate();
		this.endDate=plan.getEndDate();
		this.billRule=plan.getBillRule();
		this.status=plan.getStatus();
		this.provisionSystem=plan.getProvisionSystem();
		this.isPrepaid=plan.getIsPrepaid();
		this.isHwReq=plan.getIsHwReq();
		this.planType=plan.getPlanType();
		this.currencyId=plan.getCurrencyId();
		this.planPoid=plan.getPlanPoid();
	}
	
	public static String createPlanRequestInput(){
		
		return  "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
		"xmlns:inf=\"http://10.242.97.139:7002/infranetwebsvc/services/Infranet\">"+
		"<soapenv:Header/><soapenv:Body><inf:opcode soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
		"<opcode xsi:type=\"xsd:string\">MSO_OP_CUST_GET_CUSTOMER_INFO</opcode>"+
		"<inputXML xsi:type=\"xsd:string\"><![CDATA[<MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>"+
		"<ACCOUNT_NO>1080532299</ACCOUNT_NO><FLAGS>0</FLAGS><POID>0.0.0.1 / 0 0</POID>"+
		"<PROGRAM_NAME>OAP|csradmin</PROGRAM_NAME><USERID>0.0.0.1 /account 416152 8</USERID>"+
		"</MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>]]></inputXML></inf:opcode></soapenv:Body></soapenv:Envelope>";
		
		/*MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String SOAPAction = "http://schemas.xmlsoap.org/soap/envelope/";
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ws", "http://ws.api.le.com");
        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement("MSO_OP_CUST_GET_PLAN_DETAILS_inputFlist","ws");
		        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("MSO_FLD_PLAN_SUB_TYPE","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        soapBodyElem1 = soapBodyElem.addChildElement("MSO_FLD_PLAN_TYPE","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        soapBodyElem1 = soapBodyElem.addChildElement("POID","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        soapBodyElem1 = soapBodyElem.addChildElement("SEARCH_KEY","ws");
		        soapBodyElem1.addTextNode(this.description);
		           
		        soapBodyElem1 = soapBodyElem.addChildElement("SEARCH_TYPE","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        soapBodyElem1 = soapBodyElem.addChildElement("TYPE_OF_SERVICE","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        soapBodyElem1 = soapBodyElem.addChildElement("USERID","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        soapBodyElem1 = soapBodyElem.addChildElement("VALUE","ws");
		        soapBodyElem1.addTextNode(this.description);
		        
		        
		        MimeHeaders headers = soapMessage.getMimeHeaders();
	        headers.addHeader("SOAPAction", String.valueOf(SOAPAction) + "setStbDetail");
        soapMessage.saveChanges();
        return soapMessage;*/
		
	}
	
	
	
	public static Plan fromCelcomJson(JSONObject plan){
		try{
			String planCode=plan.optString("brm:POID");
			String[] planCodeArray=planCode.split("");
			String code=planCodeArray[2];
			String planDescription=plan.optString("brm:DESCR");
			String startDateOBRM=plan.optString("brm:CREATED_T");
			Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse(startDateOBRM);
			Date endDate=null;
			Long billRule=(long) 300;
			Long status=(long) 1;
			String provisioningSystem="Provisioning";
			boolean isPrepaid=false;
			boolean allowTopup=false;
			boolean isHwReq=false;
			long planType=210;
			int currencyId=plan.optInt("brm:CURRENCY");
			Integer currencyInt = new Integer(currencyId);
			String currency=currencyInt.toString();
			Long planPoid=Long.parseLong(code);
			return new Plan(code,planDescription,startDate,endDate,billRule,status,null,
					provisioningSystem,isPrepaid,allowTopup,isHwReq,planType,currency,planPoid);
					}
		catch(ParseException e ){
				throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
	}	

	
	
	
	
/*public static List<Plan> fromCelcomJson(String result){
	Plan planObject=null;
	List<Plan> planList=null;
	try{
		JSONObject object = new JSONObject(result);
		object=object.getJSONObject("brm:COB_OP_CUST_SEARCH_PLAN_outputFlist");
		JSONArray plansArray=null;
		if(object.optJSONArray("brm:PLAN")!=null){
			plansArray = object.optJSONArray("brm:PLAN");
		}else{
			plansArray = new JSONArray("["+object.getString("brm:PLAN")+"]");
		}
		for(int i=0;i<plansArray.length();i++){
			JSONObject plan=plansArray.getJSONObject(i);
			String planCode=plan.optString("brm:POID");
			String[] planCodeArray=planCode.split("");
			String code=planCodeArray[2];
			String planDescription=plan.optString("brm:DESCR");
			String startDateOBRM=plan.optString("brm:CREATED_T");
			Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse(startDateOBRM);
			Date endDate=null;
			Long billRule=(long) 300;
			String status=plan.optString("brm:STATUS");
			Long status=(long) 1;
			Set<PlanDetails> planDetailsList = null;
			JSONArray dealsArray;
			if(object.optJSONArray("brm:DEALS")!=null){
				dealsArray = plan.optJSONArray("brm:DEALS");
			}else{
				dealsArray = new JSONArray("["+plan.getString("brm:DEALS")+"]");
			}
			for(int j=0;j<dealsArray.length();j++){
				JSONObject deals=dealsArray.getJSONObject(j);
				String dealCode=deals.optString("brm:POID");
				String[] dealCodeArray=dealCode.split("");
				Long productId=Long.parseLong(dealCodeArray[2]);
				Long poid=productId;
				JSONObject product=deals.optJSONObject("brm:PRODUCTS");
				String productPoidObject=product.optString("brm:POID");
				String[] productPoidArray=productPoidObject.split("");
				Long serviceId=Long.parseLong(productPoidArray[2]);
				PlanDetails planDetails= new PlanDetails(productId,serviceId,poid);
				planDetailsList.add(planDetails);
			}
			String provisioningSystem="Provisioning";
			boolean isPrepaid=false;
			boolean allowTopup=false;
			boolean isHwReq=false;
			long planType=210;
			int currencyId=plan.optInt("brm:CURRENCY");
			Integer currencyInt = new Integer(currencyId);
			String currency=currencyInt.toString();
			Long planPoid=Long.parseLong(code);
			planObject=new Plan(code,planDescription,startDate,endDate,billRule,status,planDetailsList,
					provisioningSystem,isPrepaid,allowTopup,isHwReq,planType,currency,planPoid);
			planList.add(planObject);
		}
	}
	catch(JSONException |java.text.ParseException e ){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
	}
	return planList;
}	
*/}


	

	

