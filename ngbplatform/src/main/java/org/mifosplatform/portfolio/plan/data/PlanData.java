package org.mifosplatform.portfolio.plan.data;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.data.PriceData;
import org.mifosplatform.celcom.domain.PlanTypeEnum;
import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.domain.Product;

public class PlanData {

	private  Long id;
	private  Long billRule;
	private  String planCode;
	private  String planDescription;
	private  LocalDate startDate;
	private  LocalDate endDate;
	private  Long status;
	private  EnumOptionData planstatus;
	private  String productDescription;
	private  Collection<ServiceData> products;
	private  Collection<ServiceData> selectedProducts;
	private List<String> contractPeriods;
	private Collection<SubscriptionData> subscriptiondata;
	private List<BillRuleData> billRuleDatas;
	private List<EnumOptionData> planStatus,volumeTypes;
	private  String contractPeriod;
	private PlanData datas;
	private long statusname;
	private Collection<MCodeData> provisionSysData;
	private String provisionSystem;
	private String isPrepaid;
	private String allowTopup;
	private String isHwReq;
	private String volume;
	private String units;
	private String unitType;
	private Long contractId;
	private Boolean isActive=false;
	//private Integer planCount = 0;
	private List<PlanData> data = null;
	private List<Long> dealsPoid =null;
	private Set<ProductData> productsDatas;
	private List<PlanTypeEnum> PlanTypeEnum;
	private List<SearchTypeEnum> SearchTypeEnum;
	
	

	private int planCount;
	private boolean ordersFlag;
	private Collection<MCodeData> planTypeData;
	private Long planType;
	private String planTypeName;
	//private ApplicationCurrencyConfigurationData currencyData;
	private ApplicationCurrencyConfigurationData currencydata;
	private Long currencyId;
	private String currencyCode;
	private String serviceCode;
	private Long productId;
	
	private Long planPoid;
	private Long productPoid;
	private Long dealPoid;
	private List<ServiceData> services;
	private Long serviceId;
	
	private List<ProductData> productDatas;
	private List<PriceData> priceDatas;
	private List<PlanDetailData> planDetailData;
	private String dateFormat;
	private String locale;
	private String duration;
	private String productcode;
	

	private String channelname;
	private Boolean isHdChannel;
	private Long channelId;
	private String channelCategory;
	private String chargeCycle;
	private Long contractPeriodId;
	

	

	public PlanData() {
	}
	
	public PlanData(final String planCode, final Long planPoid,final Long dealPoid) {
		this.planCode = planCode;
		this.planPoid = planPoid;
		this.dealPoid = dealPoid;
	}
	
	public PlanData(Collection<ServiceData> data, List<BillRuleData> billData,List<SubscriptionData> contractPeriod, List<EnumOptionData> status,
			PlanData datas, Collection<ServiceData> selectedProducts,Collection<MCodeData> provisionSysData, List<EnumOptionData> volumeType,
			Collection<MCodeData> planTypeData,ApplicationCurrencyConfigurationData currencydata,List<ServiceData> services) {
	
		if(datas!=null){
		this.id = datas.getId();
		this.planCode = datas.getplanCode();
		this.subscriptiondata = contractPeriod;
		this.startDate = datas.getStartDate();
		this.status = datas.getStatus();
		this.billRule = datas.getBillRule();
		this.endDate = datas.getEndDate();
		this.planDescription = datas.getplanDescription();
		this.provisionSystem=datas.getProvisionSystem();
		this.isPrepaid=datas.getIsPrepaid();
		this.allowTopup=datas.getAllowTopup();
		this.volume=datas.getVolume();
		this.units=datas.getUnits();
		this.unitType=datas.getUnitType();
		this.contractPeriod=datas.getPeriod();
		this.isHwReq=datas.getIsHwReq();
		this.planType=datas.getPlanType();
		this.planTypeName=datas.getPlanTypeName();
		this.currencyId = datas.getCurrencyId();
		this.currencyCode = datas.getCurrencyCode();
		
		}
		this.products = data;
        this.provisionSysData=provisionSysData;
		this.selectedProducts = selectedProducts;
		this.billRuleDatas = billData;
		this.subscriptiondata=contractPeriod;
		this.planStatus = status;
		this.productDescription = null;
		this.services = services;
		
		this.datas = datas;
		//this.datas = null;
		this.volumeTypes=volumeType;
		this.planTypeData = planTypeData;
		this.currencydata = currencydata;

	}
	
	public PlanData(Long id, String planCode, LocalDate startDate,LocalDate endDate, Long bill_rule, String contractPeriod,
			long status, String planDescription,String provisionSys,EnumOptionData enumstatus, String isPrepaid,
			String allowTopup, String volume, String units, String unitType, Collection<ServiceData> products, Long contractId, String isHwReq,Long count, Long planType,
			String planTypeName,Long currencyId,String currencyCode,Long serviceId, String duration) {

		this.id = id;
		this.planCode = planCode;
		this.productDescription = null;
		this.startDate = startDate;
		this.status = status;
		this.billRule = bill_rule;
		this.endDate = endDate;
		this.planDescription = planDescription;
		this.services = null;
		this.billRuleDatas = null;
		this.contractPeriod = contractPeriod;
        this.provisionSystem=provisionSys;  
		this.selectedProducts = null;
		this.planstatus = enumstatus;
		this.isPrepaid=isPrepaid;
		this.allowTopup=allowTopup;
		this.volume=volume;
		this.units=units;
		this.unitType=unitType;
		this.isHwReq=isHwReq;
		this.products=products;
		this.contractId=contractId;
		this.ordersFlag=(count>0)?true:false;
		this.planType=planType;
		this.planTypeName=planTypeName;
		this.currencyId=currencyId;
		this.currencyCode=currencyCode;
		this.serviceId=serviceId;
		this.duration = duration;
	}

	public PlanData(final Long id, final String planCode, final String planDescription, String planPoid, final String dealPoid,final String isPrepaid) {
		this.id = id;
		this.planCode = planCode;
		this.planDescription = planDescription;
		if(planPoid !=null)
		this.planPoid=Long.valueOf(planPoid);
		if(dealPoid !=null)
			this.dealPoid=Long.valueOf(dealPoid);
		this.isPrepaid = isPrepaid;
		/*if(planPoid!=null)
			this.planPoid=Long.parseLong(planPoid);
		if(dealPoid!=null)
			this.dealPoid=Long.parseLong(dealPoid);*/
	}
	public PlanData(final Long id){
		this.id=id;
	}
	
	public Long getPlanType() {
		return planType;
	}


	public List<String> getContractPeriods() {
		return contractPeriods;
	}

	public String getIsHwReq() {
		return isHwReq;
	}

	public Long getContractId() {
		return contractId;
	}


	public List<PlanData> getData() {
		return data;
	}


	public PlanData(List<PlanData> datas) {
		this.data = datas;
	}

	public PlanData(Long id, String planCode, String planDescription, String isPrepaid) {
              this.id=id;
              this.planCode=planCode;
              this.planDescription=planDescription;
              this.isPrepaid = isPrepaid;
	}


	public PlanData(String code, String planDescription, Date startDate,
			Date endDate, Long status,String currency, Long planPoid) {
		// TODO Auto-generated constructor stub
		this.planCode=code;
		this.planDescription=planDescription;
		this.startDate=new LocalDate(startDate);
		this.endDate=new LocalDate(endDate);
		/*this.billRule=billRule;*/
		this.status=status;
		/*this.provisionSystem=provisioningSystem;
		this.isPrepaid=isPrepaid?"Y":"N";
		this.allowTopup=allowTopup?"Y":"N";
		this.isHwReq=isHwReq?"Y":"N";
		this.planType=planType;*/
		this.currencyId= Long.parseLong(currency);
		this.planPoid=planPoid;
		this.dateFormat="MMMM dd, yyyy";
		this.locale="en";
	}
	
	//Constructor is for PlanDetails
	public PlanData(Long code, Long productPoid, Long poid) {
		this.productId=code;
		this.productPoid=productPoid;
		this.setDealPoid(poid);
	}

	public String getProvisionSystem() {
		return provisionSystem;
	}


	public EnumOptionData getPlanstatus() {
		return planstatus;
	}

	public PlanData getDatas() {
		return datas;
	}

	public Collection<ServiceData> getSelectedProducts() {
		return selectedProducts;
	}

	public long getStatusname() {
		return statusname;
	}

	public List<EnumOptionData> getPlanStatus() {
		return planStatus;
	}

	public String getPeriod() {
		return contractPeriod;
	}

	public void setContractPeriod(List<String> contractPeriod) {
		this.contractPeriods = contractPeriod;
	}

	public List<BillRuleData> getBillRuleData() {
		return billRuleDatas;
	}

	public Long getId() {
		return id;
	}

	public String getplanCode() {
		return planCode;
	}

	public String getplanDescription() {
		return planDescription;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public Long getStatus() {
		return status;
	}

	public Collection<ServiceData> getServicedata() {
		return products;
	}

	public Long getBillRule() {
		return billRule;
	}

	public List<String> getContractPeriod() {
		return contractPeriods;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public Collection<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}


	/**
	 * @return the planCode
	 */
	public String getPlanCode() {
		return planCode;
	}


	/**
	 * @return the planDescription
	 */
	public String getPlanDescription() {
		return planDescription;
	}


	/**
	 * @return the serviceDescription
	 */
	public String getProductDescriptionn() {
		return productDescription;
	}


	/**
	 * @return the services
	 */
	public Collection<ServiceData> getServices() {
		return products;
	}


	/**
	 * @return the billRuleDatas
	 */
	public List<BillRuleData> getBillRuleDatas() {
		return billRuleDatas;
	}


	/**
	 * @return the volumeTypes
	 */
	public List<EnumOptionData> getVolumeTypes() {
		return volumeTypes;
	}


	/**
	 * @return the provisionSysData
	 */
	public Collection<MCodeData> getProvisionSysData() {
		return provisionSysData;
	}


	public Collection<MCodeData> getPlanTypeData() {
		return planTypeData;
	}


	/**
	 * @return the isPrepaid
	 */
	public String getIsPrepaid() {
		return isPrepaid;
	}


	/**
	 * @return the allowTopup
	 */
	public String getAllowTopup() {
		return allowTopup;
	}


	/**
	 * @return the volume
	 */
	public String getVolume() {
		return volume;
	}


	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}


	/**
	 * @return the unitType
	 */
	public String getUnitType() {
		return unitType;
	}


	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}


	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}




	public void setProducts(List<ServiceData> products) {
		
		this.products=products;
	}


	public void setPlanCount(int size) {
		this.planCount=size;
		
	}


	public String getPlanTypeName() {
		return planTypeName;
	}


	public void setPlanTypeName(String planTypeName) {
		this.planTypeName = planTypeName;
	}


	public Long getCurrencyId() {
		return currencyId;
	}


	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}


	public String getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public void setSelectedProducts(Collection<ServiceData> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setBillRule(Long billRule) {
		this.billRule = billRule;
	}


	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}


	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}


	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public void setStatus(Long status) {
		this.status = status;
	}


	public void setPlanstatus(EnumOptionData planstatus) {
		this.planstatus = planstatus;
	}


	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}


	public void setProducts(Collection<ServiceData> products) {
		this.products = products;
	}


	public void setContractPeriods(List<String> contractPeriods) {
		this.contractPeriods = contractPeriods;
	}


	public void setSubscriptiondata(Collection<SubscriptionData> subscriptiondata) {
		this.subscriptiondata = subscriptiondata;
	}


	public void setBillRuleDatas(List<BillRuleData> billRuleDatas) {
		this.billRuleDatas = billRuleDatas;
	}


	public void setPlanStatus(List<EnumOptionData> planStatus) {
		this.planStatus = planStatus;
	}


	public void setVolumeTypes(List<EnumOptionData> volumeTypes) {
		this.volumeTypes = volumeTypes;
	}


	public void setContractPeriod(String contractPeriod) {
		this.contractPeriod = contractPeriod;
	}


	public void setDatas(PlanData datas) {
		this.datas = datas;
	}


	public void setStatusname(long statusname) {
		this.statusname = statusname;
	}


	public void setProvisionSysData(Collection<MCodeData> provisionSysData) {
		this.provisionSysData = provisionSysData;
	}


	public void setProvisionSystem(String provisionSystem) {
		this.provisionSystem = provisionSystem;
	}


	public void setIsPrepaid(String isPrepaid) {
		this.isPrepaid = isPrepaid;
	}


	public void setAllowTopup(String allowTopup) {
		this.allowTopup = allowTopup;
	}


	public void setIsHwReq(String isHwReq) {
		this.isHwReq = isHwReq;
	}


	public void setVolume(String volume) {
		this.volume = volume;
	}


	public void setUnits(String units) {
		this.units = units;
	}


	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}


	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}


	public void setData(List<PlanData> data) {
		this.data = data;
	}


	public void setOrdersFlag(boolean ordersFlag) {
		this.ordersFlag = ordersFlag;
	}


	public void setPlanTypeData(Collection<MCodeData> planTypeData) {
		this.planTypeData = planTypeData;
	}


	public void setPlanType(Long planType) {
		this.planType = planType;
	}


	public void setCurrencydata(ApplicationCurrencyConfigurationData currencydata) {
		this.currencydata = currencydata;
	}
	
	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public Long getPlanPoid() {
		return planPoid;
	}


	public void setPlanPoid(Long planPoid) {
		this.planPoid = planPoid;
	}

	
	
	public List<Long> getDealsPoid() {
		return dealsPoid;
	}

	public void setDealsPoid(List<Long> dealsPoid) {
		this.dealsPoid = dealsPoid;
	}

	public Set<ProductData> getProductsDatas() {
		return productsDatas;
	}

	public void setProductsDatas(Set<ProductData> productsDatas) {
		System.out.println("Datas"+this.productDatas);
		this.productsDatas = productsDatas;
	}

	
	public List<PlanDetailData> getPlanDetailData() {
		return planDetailData;
	}

	public void setPlanDetailData(List<PlanDetailData> planDetailData) {
		this.planDetailData = planDetailData;
	}

	public static String obrmRequestInput(String accountNo,String poId,String userId) {
		
		StringBuffer s = new StringBuffer("<COB_OP_CUST_SEARCH_PLAN_inputFlist>"); 
	    s.append("<POID>0.0.0.1 /plan -1 0</POID>");
	    s.append("<SEARCH_KEY>0</SEARCH_KEY>");
	    s.append("<TYPE>0</TYPE>");
	    s.append("<COB_FLD_PLAN_TYPE>0</COB_FLD_PLAN_TYPE>");
	    s.append("<COB_FLD_PLAN_SUB_TYPE>0</COB_FLD_PLAN_SUB_TYPE>");
		s.append("<VALUE></VALUE>");
		s.append("<SEARCH_TYPE>0</SEARCH_TYPE>");
		s.append("<START_T>0</START_T>");
		s.append("<END_T>0</END_T>");
		s.append("</COB_OP_CUST_SEARCH_PLAN_inputFlist>");
		return s.toString();
	}

	public static String celcomRequestInput(String key,String value ,String PlanTypeEnum,String SearchTypeEnum) {
		
		StringBuffer s = new StringBuffer("<COB_OP_CUST_SEARCH_PLAN_inputFlist>"); 
	    s.append("<POID>0.0.0.1 /plan -1 0</POID>");
	    s.append("<SEARCH_KEY>1</SEARCH_KEY>");
	    s.append("<TYPE>0</TYPE>");
	    s.append("<COB_FLD_PLAN_TYPE>0</COB_FLD_PLAN_TYPE>");
	    s.append("<COB_FLD_PLAN_SUB_TYPE>0</COB_FLD_PLAN_SUB_TYPE>");
		s.append("<VALUE>"+value+"</VALUE>");
		s.append("<SEARCH_TYPE>3</SEARCH_TYPE>");
		s.append("<START_T>0</START_T>");
		s.append("<END_T>0</END_T>");
		s.append("</COB_OP_CUST_SEARCH_PLAN_inputFlist>");
		return s.toString();
	}


	public void setServiceMasterDatas(List<ServiceData> services) {
		this.services = services;	
	}
	
	public List<ServiceData> getServiceMasterDatas() {
		return services;
	}
	
	public Long getServiceId() {
		return serviceId;
	}


	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	
	public List<ProductData> getProductDatas() {
		return productDatas;
	}

	public void setProductDatas(List<ProductData> productDatas) {
		this.productDatas = productDatas;
	}

	public List<PriceData> getPriceDatas() {
		return priceDatas;
	}

	public void setPriceDatas(List<PriceData> priceDatas) {
		this.priceDatas = priceDatas;
	}

	public static PlanData fromCelcomJson(JSONObject plan) {
		PlanData planDataObject;
		try{
				String planCode=plan.optString("brm:POID");
				String[] planCodeArray=planCode.split(" ");
				String code=planCodeArray[2];
				String planDescription=plan.optString("brm:NAME");
				String startDateOBRM=plan.optString("brm:CREATED_T");
				Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse(startDateOBRM);
				Date endDate=null;
				Long status=(long) 1;
				int currencyId=plan.optInt("brm:CURRENCY");
				Integer currencyInt = new Integer(currencyId);
				String currency=currencyInt.toString();
				Long planPoid=Long.parseLong(code);
				planDataObject=new PlanData(code,planDescription,startDate,endDate,status,currency,planPoid);
			
		}
		catch(java.text.ParseException e ){
				throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
		return planDataObject;
	}
	public Long getDealPoid() {
		return dealPoid;
	}

	public void setDealPoid(Long dealPoid) {
		this.dealPoid = dealPoid;
	}
	public PlanData(final List<PlanTypeEnum>PlanTypeEnum,final List<SearchTypeEnum> SearchTypeEnum) {
		
      this.PlanTypeEnum=PlanTypeEnum;
      this.SearchTypeEnum=SearchTypeEnum;


}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

	public void setIsHdChannel(Boolean isHdChannel) {
		this.isHdChannel = isHdChannel;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	
	public void setChannelCategory(String channelCategory) {
		this.channelCategory = channelCategory;
	}
	
	public void setChargeCycle(String chargeCycle) {
		this.chargeCycle = chargeCycle;
	}
	
	public String getChargeCycle() {
		return chargeCycle;
	}
	
	public void setContractPeriodId(Long contractPeriodId) {
		this.contractPeriodId = contractPeriodId;
	}
	
	public Long getContractPeriodId() {
		return contractPeriodId;
	}
}
