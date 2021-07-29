package org.mifosplatform.celcom.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.chargecode.service.ChargeCodeReadPlatformService;
import org.mifosplatform.celcom.api.CelcomApiConstants;
import org.mifosplatform.celcom.domain.PlanTypeEnum;
import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.billingmaster.service.BillMasterReadPlatformService;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.PlanDetailData;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CelcomReadPlatformServiceImpl implements CelcomReadPlatformService {

	private final ClientReadPlatformService clientReadPlatformService;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final CelcomReadWriteConsiliatrService celcomReadWriteConsiliatrService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final OfficeRepository OfficeRepository;
	private final BillMasterReadPlatformService billMasterReadPlatformService;
	private final ClientRepository clientRepository;
	private final ChargeCodeReadPlatformService chargeCodeReadPlatformService;
	
		@Autowired
	public CelcomReadPlatformServiceImpl(
			ClientReadPlatformService clientReadPlatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			final CelcomReadWriteConsiliatrService celcomReadWriteConsiliatrService,final OfficeReadPlatformService officeReadPlatformService,
			final OfficeRepository OfficeRepository,BillMasterReadPlatformService billMasterReadPlatformService,
			final ClientRepository clientRepository, final ChargeCodeReadPlatformService chargeCodeReadPlatformService) {

		this.clientReadPlatformService = clientReadPlatformService;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.celcomReadWriteConsiliatrService = celcomReadWriteConsiliatrService;
		this.officeReadPlatformService =officeReadPlatformService;
		this.OfficeRepository =OfficeRepository;
		this.billMasterReadPlatformService=billMasterReadPlatformService;
		this.clientRepository = clientRepository;
		this.chargeCodeReadPlatformService =chargeCodeReadPlatformService;
	}

	@Override
	public ClientData retriveClientTotalData(String key, String value) {
		try {
			ClientData clientData =this.clientReadPlatformService.retrieveOne(key,value);
			String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.READCLIENT_OPCODE,
							clientData.celcomRequestInput(0));
			if (result != null) {
				clientData = ClientData.fromCelcomJson(result, clientData);
				clientData.setClientServiceData(this.clientServiceReadPlatformService.retriveClientServices(clientData.getId()));
	        	clientData.setOneTimeSaleData(this.oneTimeSaleReadPlatformService.retrieveClientOneTimeSalesData(clientData.getId()));
	        	clientData.setOrderData(this.orderReadPlatformService.retrieveClientOrderDetails(clientData.getId()));
				
				
				/*
				// client service data preparation
				List<ClientServiceData> clientServiceDatas = this.clientServiceReadPlatformService.retriveClientServices(clientData.getId());
				
				if (!clientServiceDatas.isEmpty()) {
					clientServiceDatas = ClientServiceData.fromCelcomJson(result,clientServiceDatas);
					clientData.setClientServiceData(clientServiceDatas);
				}

				// onetimesale data preparation
				List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveClientOneTimeSalesData(clientData.getId());
				if (!oneTimeSaleDatas.isEmpty()) {
					oneTimeSaleDatas = OneTimeSaleData.fromCelcomJson(result,oneTimeSaleDatas);
					clientData.setOneTimeSaleData(oneTimeSaleDatas);
				}

				// orders data preparation
				List<OrderData> orderDatas = this.orderReadPlatformService.retrieveClientOrderDetails(clientData.getId());
				if (!orderDatas.isEmpty()) {
					orderDatas = OrderData.fromCelcomJson(result, orderDatas);
					clientData.setOrderData(orderDatas);
				}*/

				return clientData;
			} else {
				throw new PlatformDataIntegrityException("", "");
			}
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException(
					"error.msg.jsonexception.occured", e.getMessage());
		} 
	}

	
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<PlanData> retrivePlans(String key, String value,String PlanTypeEnum,String SearchTypeEnum,String searchType) {
		JSONArray plansArray=null;JSONObject planObject = null;
		PlanData planData = null;JSONArray dealsArray = null;
		List<PlanData> plans= new ArrayList<PlanData>();
		try	{
			String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.READPLAN_OPCODE, PlanData.celcomRequestInput(key,value, SearchTypeEnum,PlanTypeEnum));
			if (result != null) {
				JSONObject resultJsonObject = new JSONObject(result);
				resultJsonObject = resultJsonObject.optJSONObject("brm:COB_OP_CUST_SEARCH_PLAN_outputFlist");
				plansArray = resultJsonObject.optJSONArray("brm:PLAN");
				if(plansArray == null){
					plansArray = new JSONArray("["+resultJsonObject.optJSONObject("brm:PLAN")+"]");
				}
				for(int i=0;i<plansArray.length();i++){
					planObject = plansArray.optJSONObject(i);
					Set<ProductData> productDatas = new HashSet<ProductData>();
					List<PlanDetailData> planDetails = new ArrayList<PlanDetailData>();
					planData = PlanData.fromCelcomJson(planObject);
					dealsArray = planObject.optJSONArray("brm:DEALS");
					if(dealsArray ==null){
						dealsArray = new JSONArray("["+planObject.optJSONObject("brm:DEALS")+"]");
					}
					JSONObject dealsObject =  null;
					for(int j=0;j<dealsArray.length();j++){
						dealsObject = dealsArray.optJSONObject(j);
						Map<String,Object> dealsAndProducts = fromCelcomJsonDealPoid(dealsObject);
						planDetails.addAll((List<PlanDetailData>)dealsAndProducts.get("deals"));
						productDatas.addAll((Set<ProductData>)dealsAndProducts.get("products"));
						if(dealsAndProducts.containsKey("isPrepaid")){
							planData.setIsPrepaid((String)dealsAndProducts.get("isPrepaid"));
						}
					}
					planData.setProductsDatas(productDatas);
					planData.setPlanDetailData(planDetails);
					plans.add(planData);
				}
				
					
			}
			return plans;
		}catch(JSONException e ){
				throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
	}
	
	public Map<String,Object> fromCelcomJsonDealPoid(JSONObject deal) {
		Map<String,Object> deals = new HashMap();
		Long dealPoid;JSONArray productsArray = null;PlanDetailData planDetail = null;
		ProductData productData = null;Set<ProductData> productDatas = new HashSet<ProductData>();
		List<PlanDetailData> planDetails =new ArrayList<PlanDetailData>();
		
		try{
			String isPrepaid=null;
			dealPoid = retrivePoId(deal.optString("brm:POID"));
			productsArray = deal.optJSONArray("brm:PRODUCTS");
			if(productsArray == null){
				productsArray = new JSONArray("["+deal.optJSONObject("brm:PRODUCTS")+"]");
			}
			JSONObject productObject = null;
			for(int i=0;i<productsArray.length();i++){
				productObject = productsArray.optJSONObject(i);
				
				productData = this.fromCelcomJson(productObject);
				int priority=productData.getPriority();
				if((1000<=priority)&&(priority<=3000)){
					isPrepaid="Y";
					deals.put("isPrepaid", isPrepaid);
					
				}
				productDatas.add(productData);
				planDetails.add(new PlanDetailData(dealPoid, productData.getProductPoid()));
			}
			deals.put("products", productDatas);
			deals.put("deals",planDetails);
			;
			return deals;
		}
		catch(JSONException e ){
				throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
	}

	private static Long retrivePoId(String dealPoId) {
		String[] args = dealPoId.split(" ");
		dealPoId = args[2];
		return Long.valueOf(dealPoId);
		
	}
	
	public  ProductData fromCelcomJson(JSONObject product) {
		try{
			String productPoid=product.optString("brm:POID");
			String[] productPoidArray=productPoid.split(" ");
			Long poid=Long.parseLong(productPoidArray[2]);
			String productCode=poid.toString();
			Integer priority=product.optInt("brm:PRIORITY");
			String description=product.getString("brm:NAME");
			String chargeCode, contractPeriod = null;
			if((1000<=priority)&&(priority<=3000)){
				if(product.optString("brm:EVENT_TYPE")!=null&&!(product.optString("brm:EVENT_TYPE").equalsIgnoreCase(""))){
					ChargeCodeData chargeCodeData = this.retrieveChargeDataOnEventType(product.optString("brm:EVENT_TYPE"));
					chargeCode = chargeCodeData.getChargeCode();
					contractPeriod = chargeCodeData.getContractPeriod();
				}else{
					chargeCode = "MSC";
					contractPeriod = "1 Month";
				}
			}else{
				chargeCode = "MSC";
				contractPeriod = "1 Month";
			}
			BigDecimal price = new BigDecimal(product.getLong("brm:SCALED_AMOUNT"));
			return new ProductData(productCode,description,poid, priority,price,contractPeriod, chargeCode);
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
	}
	
	ChargeCodeData retrieveChargeDataOnEventType(String eventType){
		switch (eventType) {
        case "/event/billing/product/fee/cycle/cycle_forward_monthly":
        		eventType = "1";
                break;
        case "/event/billing/product/fee/cycle/cycle_forward_bimonthly":
        eventType = "2";
        break;
        case "/event/billing/product/fee/cycle/cycle_forward_quarterly":  
        eventType = "3";
        break;
        case "/event/billing/product/fee/cycle/cycle_forward_semiannual": 
        eventType = "6";
        break;
        case "/event/billing/product/fee/cycle/cycle_forward_annual": 
        eventType = "12";
        break;
        default: 
        	return null;
		}
		return this.chargeCodeReadPlatformService.retrieveChargeCodeAndContractPeriodUsingPlanDuration(Long.parseLong(eventType));
	}
	
	
	@Override
	public List<PlanTypeEnum> retrievePlanTypeEnum() {
		final PlanTypeEnum All = PlanTypeEnum.fromInt(0);
		final PlanTypeEnum Hardware = PlanTypeEnum.fromInt(1);
		final PlanTypeEnum Subscription = PlanTypeEnum.fromInt(2);
		final PlanTypeEnum OneTimeCharges = PlanTypeEnum.fromInt(3);
		final List<PlanTypeEnum> PlanType = Arrays.asList(All,Hardware,Subscription,OneTimeCharges);
		return PlanType;
	}
	
	@Override
	public List<SearchTypeEnum> retrieveSearchTypeEnum() {
		List<SearchTypeEnum> SearchType = new ArrayList<SearchTypeEnum>();
		for(int i=0;i<=3;i++){
			SearchType.add(SearchTypeEnum.fromInt(i));
		}
		/*final SearchTypeEnum EQUALS = SearchTypeEnum.fromInt(0);
		final SearchTypeEnum STARTS_WITH = SearchTypeEnum.fromInt(1);
		final SearchTypeEnum ENDS_WITH = SearchTypeEnum.fromInt(2);
		final SearchTypeEnum CONTAINS = SearchTypeEnum.fromInt(3);
		final List<SearchTypeEnum> SearchType = Arrays.asList(EQUALS,STARTS_WITH,ENDS_WITH,CONTAINS);*/
		return SearchType;
	}

	@Override
	public FinancialTransactionsData retriveClientBillData(String key,
			String value) {
		// TODO Auto-generated method stub
		ClientData clientData =this.clientReadPlatformService.retrieveOne(key,value);
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.READCLIENT_OPCODE,
						clientData.celcomRequestInput(8));
		
		
		
		return null;
	}
	@Override
	public OfficeData retriveOfficeData(OfficeData officeData) {
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.READOFFICE_OPCODE,
				officeData.celcomRequestInput(0));
		officeData.setBalanceAmount(this.retriveCURRENCYSECONDARY(result));
		officeData.setWalletAmount(this.retriveWalletAmount(result));
		
		return  officeData;
	}
	
	private String retriveWalletAmount(String result) {
		try{
			JSONObject object = new JSONObject(result);
			JSONArray billInfoArray=null;
			JSONObject object1 = null;
			String walletAmount = null;
			if(object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist")!=null){
				object = object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist");
			}
			billInfoArray = object.optJSONArray("brm:BILLINFO");
			if(billInfoArray == null){
				billInfoArray = new JSONArray("["+object.optJSONObject("brm:BILLINFO")+"]");
			}
			
			for(int i=0;i<billInfoArray.length();i++){
				object1 = billInfoArray.optJSONObject(i);
				String billInfo=object1.optString("brm:BILLINFO_ID");
				if(object1.optString("brm:BILLINFO_ID").equalsIgnoreCase("PREPAID_WALLET")){
					walletAmount=object1.optString("brm:CURRENT_BAL");
				}	
			}
			
			return walletAmount;
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	
	
	
	
	private String retriveCURRENCYSECONDARY(String result) {
		try{
			/*result is {"brm:COB_OP_CUST_CREATE_SUBSCRIBER_outputFlist":{"brm:LOGIN":"CR-1522832556893",
				"brm:DESCR":"Subscriber Registration Successful",
				"xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
				"brm:POID":"0.0.0.1 /account 337977 0",
				"brm:BUSINESS_TYPE":90000000,"brm:ACCOUNT_NO":"CR-1522832556893","brm:STATUS":0}}*/
			JSONObject object = new JSONObject(result);
			JSONArray billInfoArray=null;
			JSONObject object1 = null;
			String balanceAmount = null;
			
			if(object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist")!=null){
				object = object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist");
			}
			
			billInfoArray = object.optJSONArray("brm:BILLINFO");
			if(billInfoArray == null){
				billInfoArray = new JSONArray("["+object.optJSONObject("brm:BILLINFO")+"]");
			}
			
			for(int i=0;i<billInfoArray.length();i++){
				object1 = billInfoArray.optJSONObject(i);
				String billInfo=object1.optString("brm:BILLINFO_ID");
				if(object1.optString("brm:BILLINFO_ID").equalsIgnoreCase("BU")){
					balanceAmount=object1.optString("brm:CURRENT_BAL");
				}	
			}
			
			return balanceAmount;
			
			/*String currencysecondary=object.optString("brm:CURRENCY_SECONDARY");
			return currencysecondary;*/
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}

	@Override
	public List<BillDetailsData> retriveBillDetails(SearchSqlQuery searchCodes,Long clientId) {
		/*List<BillDetailsData> billDetailsData =this.billMasterReadPlatformService.retrieveStatements(searchCodes,clientId);
		String poId=null;
		for(BillDetailsData billDetails:billDetailsData){
			poId=billDetails.getPoId();
			break;
		}*/
		String clientPoid = this.clientRepository.findOne(clientId).getPoid();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.BILLDETAILS_OPCODE,BillDetailsData.celcomRequestInput(clientPoid));
		return BillDetailsData.fromJson(result);
	}
}	