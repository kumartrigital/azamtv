package org.mifosplatform.celcom.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.celcom.api.CelcomApiConstants;
import org.mifosplatform.finance.adjustment.data.AdjustmentData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMasterData;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.domain.Address;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.mifosplatform.organisation.mapping.service.ChannelMappingReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAddress;
import org.mifosplatform.organisation.office.domain.OfficeAddressRepository;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.officeadjustments.data.OfficeAdjustmentData;
import org.mifosplatform.organisation.officepayments.data.OfficePaymentData;
import org.mifosplatform.organisation.partneragreement.data.AgreementData;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.data.ClientDataValidator;
import org.mifosplatform.portfolio.client.domain.AccountNumberGenerator;
import org.mifosplatform.portfolio.client.domain.AccountNumberGeneratorFactory;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformServiceImpl;
import org.mifosplatform.portfolio.clientdiscount.data.ClientDiscountData;
import org.mifosplatform.portfolio.clientdiscount.domain.ClientDiscount;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.order.data.BillPlanData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderLineRepository;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.product.domain.ProductRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


import org.mifosplatform.finance.officebalance.domain.OfficeBalance;



@Service
public class CelcomWritePlatformServiceImpl implements CelcomWritePlatformService{

	private final OfficeRepository officeRepository;
	private final CelcomReadWriteConsiliatrService celcomReadWriteConsiliatrService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final ProductRepository productRepository;
	private final PlanRepository planRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final PriceRepository priceRepository;
	private final PlatformSecurityContext context;
	private final ServiceMasterRepository serviceMasterRepository;
	private final ProvisioningReadPlatformService provisioningReadPlatformService;
	private final OrderLineRepository orderLineRepository;
	private final OfficeAddressRepository addressRepository;
	private final ClientRepositoryWrapper clientRepository;
	private final ConfigurationRepository configurationRepository;
	private final SelfCareRepository selfCareRepository;
	private final ClientDataValidator fromApiJsonDeserializer;
	private final AddressReadPlatformService addressReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory;
	private final ClientReadPlatformService clientReadPlatformService;
	private final CodeValueRepository codeValueRepository;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ChannelMappingReadPlatformService channelMappingReadPlatformService;
	
	
	@Autowired
	public CelcomWritePlatformServiceImpl(OfficeRepository officeRepository,
			final CelcomReadWriteConsiliatrService celcomReadWriteConsiliatrService,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final ProductRepository productRepository,
			final PlanRepository planRepository,
			final FromJsonHelper fromApiJsonHelper,
			final PriceRepository priceRepository,
			final PlatformSecurityContext context,
			final ServiceMasterRepository serviceMasterRepository,
			final ProvisioningReadPlatformService provisioningReadPlatformService,
			final OrderLineRepository orderLineRepository, final OfficeAddressRepository addressRepository,
			final ClientRepositoryWrapper clientRepository, final ConfigurationRepository configurationRepository,
			final SelfCareRepository selfCareRepository,
			final ClientDataValidator fromApiJsonDeserializer, 
			final AddressReadPlatformService addressReadPlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory,
			final ClientReadPlatformService clientReadPlatformService, 
			final CodeValueRepository codeValueRepository,
			final OfficeReadPlatformService officeReadPlatformService,
			final ChannelMappingReadPlatformService channelMappingReadPlatformService){
		
		this.officeRepository=officeRepository;
		this.celcomReadWriteConsiliatrService = celcomReadWriteConsiliatrService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.productRepository=productRepository;
		this.planRepository=planRepository;
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.priceRepository=priceRepository;
		this.context=context;
		this.serviceMasterRepository=serviceMasterRepository;
		this.provisioningReadPlatformService = provisioningReadPlatformService;
		this.orderLineRepository = orderLineRepository;
		this.addressRepository = addressRepository;
		this.clientRepository = clientRepository;
		this.configurationRepository = configurationRepository;
		this.selfCareRepository = selfCareRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.addressReadPlatformService=addressReadPlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.accountIdentifierGeneratorFactory = accountIdentifierGeneratorFactory;
		this.clientReadPlatformService = clientReadPlatformService;
		this.codeValueRepository = codeValueRepository;
		this.officeReadPlatformService = officeReadPlatformService;
		this.channelMappingReadPlatformService = channelMappingReadPlatformService;
}
	
	@Override
	public CommandProcessingResult createClient(JsonCommand jsonCommand) {
			String resourceId=null,accountNo =null;
			final Long officeId = jsonCommand.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);
            final Office clientOffice = this.officeRepository.findOne(officeId);

            if (clientOffice == null) { throw new OfficeNotFoundException(officeId); }
            String priValue = this.accountNumberPriRequirValue();
            Client client = Client.createNew(clientOffice, null, jsonCommand,priValue);
            client.setParentId(jsonCommand.longValueOfParameterNamed("parentId"));
            String logedInUser=this.context.authenticatedUser().getUsername();
            final JsonArray addressArray = jsonCommand.arrayOfParameterNamed("address").getAsJsonArray();
            
            JsonElement primaryAddressElement= addressArray.get(0);
            JsonElement billingAddressElement=addressArray.get(1);
            //client.setAccountNumber(this.retriveAccountNo());
            client.updateAccountNo(this.retriveAccountNo(),this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.NUMBER_GENERATOR));
            if(jsonCommand.hasParameter("idKey")){
            	client.setIdkey(this.codeValueRepository.findOne(jsonCommand.longValueOfParameterNamed("idKey")));
            }
            Address primaryAddress= this.extractAddressForCelcom(client.getId(), primaryAddressElement);
            Address billingAddress=this.extractAddressForCelcom(client.getId(), billingAddressElement);
            String parentPoId=jsonCommand.stringValueOfParameterNamed("parentPoId");
            String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CREATECLIENT_OPCODE,client.celcomRequestInput(logedInUser, primaryAddress, billingAddress,parentPoId));
			if(result !=null){
				resourceId = this.retriveClientPoid(result);
				accountNo = this.retriveAccountNo(result);
			}
				
			return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).withTransactionId(accountNo).build();
	}
	@Override
	public CommandProcessingResult createOffice(JsonCommand command) {
		
		String resourceId = null;
		String transactionId=null;
        Office ofice = Office.fromJson(null, command);
        
        OfficeAddress addres =OfficeAddress.fromJson(command,ofice);
        ofice.setOfficeAddress(addres);
        ofice.setDasType(this.codeValueRepository.findOne(command.longValueOfParameterNamed("dasType")));
        String logedInUser= context.authenticatedUser().getUsername();
        
        Long parentId = command.longValueOfParameterNamed("parentId");
       
        ofice.setParent(this.officeRepository.findOne(parentId));
        ofice.updateExternalId(this.officeReadPlatformService.retriveMaxCountId(ofice.getOfficeType(), ofice.getParent().getHierarchy()));
        String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CREATEOFFICE_OPCODE, ofice.celcomRequestInput(logedInUser));
		if(result !=null){
			resourceId = this.retriveClientPoidforOffice(result);
			transactionId=this.retriveSettlementPoidforOffice(result);
		}
		return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).withTransactionId(transactionId).build();
		
	}
	
	@Override
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command) {
		String resourceId =null;
	    Set<String> substances = null;
	    String orderNo = this.retriveOrderNo(command.getClientId());
        ClientServiceData clientServiceData = ClientServiceData.fromJsonForCelcom(command);
        String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CREATECLIENTSIMPLEACTIVATION_OPCODE,clientServiceData.celcomRequestInput(logedInUser,orderNo));
		if(result !=null){
			resourceId = this.retriveClientServicePoid(result);
			substances = this.retrivePurchaseProductPoid(result);
		}
			
		return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).withSubstances(substances).build();
}
	
	
	private String retriveOrderNo(Long clientId) {
		Configuration configurationProperty = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_RANDAM_NUMBER_GENERATE);
		if(configurationProperty != null && configurationProperty.isEnabled()) {
			return String.valueOf((1000+new Random().nextInt(9000)));
		}else{
			Long orderMaxId = this.orderReadPlatformService.retriveMaxOrderId();  
			String packageId = clientId.toString()+orderMaxId;
			final AccountNumberGenerator orderNoGenerator = this.accountIdentifierGeneratorFactory.determineClientAccountNoGenerator(Long.parseLong(packageId));
			/*return Order.updateOrderNumber(orderNoGenerator.generate(),this.mCodeReadPlatformService
							.getCodeValue(CodeNameConstants.NUMBER_GENERATOR));*/
			return orderNoGenerator.generate();
		}
	}
	
	private String retriveAccountNo() {
		Configuration configurationProperty = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_RANDAM_NUMBER_GENERATE);
		if(configurationProperty != null && configurationProperty.isEnabled()) {
			return String.valueOf((1000+new Random().nextInt(9000)));
		}else{
			Long clientMaxId = this.clientReadPlatformService.retriveMaxClientId();
			final AccountNumberGenerator clientNoGenerator = this.accountIdentifierGeneratorFactory.determineClientAccountNoGenerator((clientMaxId+200000));
			return clientNoGenerator.generate();
        }
	}
	
	
	private Set<String> retrivePurchaseProductPoid(String result) {
		Set<String> substances= new HashSet<String>();
		JSONArray plansArray=null;JSONObject planObject = null;
		try{
			/*{"brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist":{"brm:SERVICE_OBJ":"0.0.0.1 /service/tv 373577 0",
			"brm:DESCR":"Service Creation Successful","xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
			"brm:POID":"0.0.0.1 /account 364859 8","brm:SERVICE_INFO":{"brm:SERVICES":
			{"elem":0,"brm:COB_FLD_CABLE_INFO":{"brm:COB_FLD_AGREEMENT_NO":"MANTHAN-201","brm:CONN_TYPE":0,
			"brm:COB_FLD_INSTALLATION_STATUS":0,"brm:CITY":"BANGALORE","brm:COB_FLD_LEGACY_AGR_NO":"",
			"brm:COB_FLD_SALESMAN_OBJ":"0.0.0.0  0 0","brm:COB_FLD_CARF_ID":"","brm:COB_FLD_CAS_SUBSCRIBER_ID":"GOSPEL-001",
			"brm:COB_FLD_ADDRESS_VERIFIED":0,"brm:BILL_WHEN":1,"brm:COB_FLD_CARF_RECEIVED":0,"brm:COB_FLD_NETWORK_NODE":"GOSPEL"},
			"brm:LOGIN":"CR-1523168306421","brm:DEVICES":[{"elem":1,"brm:NAME":"SCS004"},{"elem":0,"brm:NAME":"STBS004"}],
			"brm:STATUS":10100}},"brm:PLAN_LIST_CODE":{"brm:PLAN":{"elem":0,"brm:PRODUCT_OBJ":"0.0.0.1 /product 160521 5",
				"brm:DEAL_OBJ":"0.0.0.1 /deal 163593 0","brm:PLAN_OBJ":"0.0.0.1 /plan 159881 2",
			"brm:OFFERING_OBJ":"0.0.0.1 /purchased_product 372937 0","brm:PACKAGE_ID":1917}},
			"brm:STATUS":0}}*/
	
			JSONObject resultJsonObject = new JSONObject(result);
			if(resultJsonObject.has("brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist")){
				resultJsonObject = resultJsonObject.optJSONObject("brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist");
			}
			else if(resultJsonObject.optJSONObject("brm:COB_OP_CUST_ADD_PLAN_outputFlist")!=null){
				resultJsonObject=resultJsonObject.optJSONObject("brm:COB_OP_CUST_ADD_PLAN_outputFlist");
			}
			
			JSONObject planListCode=resultJsonObject.optJSONObject("brm:PLAN_LIST_CODE");
			plansArray = planListCode.optJSONArray("brm:PLAN");
			if(plansArray==null){
				plansArray = new JSONArray("["+planListCode.optJSONObject("brm:PLAN")+"]");
			}
			for(int i=0;i<plansArray.length();i++){
				planObject=plansArray.getJSONObject(i);
				String orderNo =  String.valueOf(planObject.optLong("brm:PACKAGE_ID"));
				String ProductPoId = this.retrivePoId(planObject.optString("brm:PRODUCT_OBJ"));
				String purchaseProductPoId = this.retrivePoId(planObject.optString("brm:OFFERING_OBJ"));
				String planPoId = this.retrivePoId(planObject.optString("brm:PLAN_OBJ"));
				System.out.println("substanc is : "+orderNo+"_"+planPoId+"_"+ ProductPoId +"_"+purchaseProductPoId);
				substances.add(orderNo+"_"+planPoId+"_"+ ProductPoId +"_"+purchaseProductPoId);
			}
			return substances;
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	private Set<String> retrivePurchaseProductPoidForChangePlan(String result) {
		Set<String> substances= new HashSet<String>();
		JSONArray plansArray=null;JSONObject planObject = null;
		try{
			/*{"brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist":{"brm:SERVICE_OBJ":"0.0.0.1 /service/tv 373577 0",
			"brm:DESCR":"Service Creation Successful","xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
			"brm:POID":"0.0.0.1 /account 364859 8","brm:SERVICE_INFO":{"brm:SERVICES":
			{"elem":0,"brm:COB_FLD_CABLE_INFO":{"brm:COB_FLD_AGREEMENT_NO":"MANTHAN-201","brm:CONN_TYPE":0,
			"brm:COB_FLD_INSTALLATION_STATUS":0,"brm:CITY":"BANGALORE","brm:COB_FLD_LEGACY_AGR_NO":"",
			"brm:COB_FLD_SALESMAN_OBJ":"0.0.0.0  0 0","brm:COB_FLD_CARF_ID":"","brm:COB_FLD_CAS_SUBSCRIBER_ID":"GOSPEL-001",
			"brm:COB_FLD_ADDRESS_VERIFIED":0,"brm:BILL_WHEN":1,"brm:COB_FLD_CARF_RECEIVED":0,"brm:COB_FLD_NETWORK_NODE":"GOSPEL"},
			"brm:LOGIN":"CR-1523168306421","brm:DEVICES":[{"elem":1,"brm:NAME":"SCS004"},{"elem":0,"brm:NAME":"STBS004"}],
			"brm:STATUS":10100}},"brm:PLAN_LIST_CODE":{"brm:PLAN":{"elem":0,"brm:PRODUCT_OBJ":"0.0.0.1 /product 160521 5",
				"brm:DEAL_OBJ":"0.0.0.1 /deal 163593 0","brm:PLAN_OBJ":"0.0.0.1 /plan 159881 2",
			"brm:OFFERING_OBJ":"0.0.0.1 /purchased_product 372937 0","brm:PACKAGE_ID":1917}},
			"brm:STATUS":0}}*/
	
			JSONObject resultJsonObject = new JSONObject(result);
			resultJsonObject = resultJsonObject.optJSONObject("brm:COB_OP_CUST_CHANGE_PLAN_outputFlist");
			plansArray = resultJsonObject.optJSONArray("brm:PLAN");
			if(plansArray==null){
				plansArray = new JSONArray("["+resultJsonObject.optJSONObject("brm:PLAN")+"]");
			}
				planObject=plansArray.getJSONObject(1);
				String orderNo =  String.valueOf(planObject.optLong("brm:PACKAGE_ID"));
				String ProductPoId = this.retrivePoId(planObject.optString("brm:PRODUCT_OBJ"));
				String purchaseProductPoId = this.retrivePoId(planObject.optString("brm:OFFERING_OBJ"));
				String planPoId = this.retrivePoId(planObject.optString("brm:PLAN_OBJ"));
				System.out.println("substanc is : "+orderNo+"_"+planPoId+"_"+ ProductPoId +"_"+purchaseProductPoId);
				substances.add(orderNo+"_"+planPoId+"_"+ ProductPoId +"_"+purchaseProductPoId);
			return substances;
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	private String retriveClientServicePoid(String result) {
		try{
			/*{"brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist":{"brm:SERVICE_OBJ":"0.0.0.1 /service/tv 373577 0",
			"brm:DESCR":"Service Creation Successful","xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
			"brm:POID":"0.0.0.1 /account 364859 8","brm:SERVICE_INFO":{"brm:SERVICES":
			{"elem":0,"brm:COB_FLD_CABLE_INFO":{"brm:COB_FLD_AGREEMENT_NO":"MANTHAN-201","brm:CONN_TYPE":0,
			"brm:COB_FLD_INSTALLATION_STATUS":0,"brm:CITY":"BANGALORE","brm:COB_FLD_LEGACY_AGR_NO":"",
			"brm:COB_FLD_SALESMAN_OBJ":"0.0.0.0  0 0","brm:COB_FLD_CARF_ID":"","brm:COB_FLD_CAS_SUBSCRIBER_ID":"GOSPEL-001",
			"brm:COB_FLD_ADDRESS_VERIFIED":0,"brm:BILL_WHEN":1,"brm:COB_FLD_CARF_RECEIVED":0,"brm:COB_FLD_NETWORK_NODE":"GOSPEL"},
			"brm:LOGIN":"CR-1523168306421","brm:DEVICES":[{"elem":1,"brm:NAME":"SCS004"},{"elem":0,"brm:NAME":"STBS004"}],
			"brm:STATUS":10100}},"brm:PLAN_LIST_CODE":{"brm:PLAN":{"elem":0,"brm:PRODUCT_OBJ":"0.0.0.1 /product 160521 5",
				"brm:DEAL_OBJ":"0.0.0.1 /deal 163593 0","brm:PLAN_OBJ":"0.0.0.1 /plan 159881 2",
			"brm:OFFERING_OBJ":"0.0.0.1 /purchased_product 372937 0","brm:PACKAGE_ID":1917}},
			"brm:STATUS":0}}*/
			JSONObject object = new JSONObject(result);
			
			if(object.optJSONObject("brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist")!=null){
				object = object.optJSONObject("brm:COB_OP_CUST_ACTIVATE_SERVICE_outputFlist");
			}
			else if(object.optJSONObject("brm:COB_OP_CUST_ADD_PLAN_outputFlist")!=null){
				object=object.optJSONObject("brm:COB_OP_CUST_ADD_PLAN_outputFlist");
			}
			return this.retrivePoId(object.optString("brm:SERVICE_OBJ"));
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}

	}
	
	
	
	private String retriveClientPoid(String result) {
		try{
			/*result is {"brm:COB_OP_CUST_CREATE_SUBSCRIBER_outputFlist":{"brm:LOGIN":"CR-1522832556893",
				"brm:DESCR":"Subscriber Registration Successful",
				"xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
				"brm:POID":"0.0.0.1 /account 337977 0",
				"brm:BUSINESS_TYPE":90000000,"brm:ACCOUNT_NO":"CR-1522832556893","brm:STATUS":0}}*/
			JSONObject object = new JSONObject(result);
			
			if(object.optJSONObject("brm:COB_OP_CUST_CREATE_SUBSCRIBER_outputFlist")!=null){
				object = object.optJSONObject("brm:COB_OP_CUST_CREATE_SUBSCRIBER_outputFlist");
			}
			else if(object.optJSONObject("brm:COB_OP_CUST_ADD_PLAN_outputFlist")!=null){
				object=object.optJSONObject("brm:COB_OP_CUST_ADD_PLAN_outputFlist");
			}
			
			return this.retrivePoId(object.optString("brm:POID"));
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	private String retrivePoId(String returnValue){
		if(returnValue !=null){
			String[] args = returnValue.split(" ");
			returnValue = args[2];
			System.out.println(returnValue);
		}else{
			throw new PlatformDataIntegrityException("poid.invalid","invalid poid","invalid poid","invalid poid");	
		}
		return returnValue;	
	}
	private String retriveClientPoidforOffice(String result) {
		try{
			/*{"brm:COB_OP_CUST_CREATE_COMPANY_outputFlist":{"brm:LOGIN":1523168129897,
				"brm:DESCR":"Company Registration Successful",
				"xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
				"brm:POID":"0.0.0.1 /account 371928 0","brm:BUSINESS_TYPE":30000000,
				"brm:ACCOUNT_NO":1523168129897,"brm:STATUS":0}}*/
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:COB_OP_CUST_CREATE_COMPANY_outputFlist");
			String returnValue = object.optString("brm:POID");
			if(returnValue !=null){
				String[] args = returnValue.split(" ");
				returnValue = args[2];
				System.out.println(returnValue);
			}else{
				throw new PlatformDataIntegrityException("poid.invalid","","","");	
			}
			return returnValue;	
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	private String retriveSettlementPoidforOffice(String result) {
		try{
			/*{"brm:COB_OP_CUST_CREATE_COMPANY_outputFlist":{"brm:LOGIN":1523168129897,
				"brm:DESCR":"Company Registration Successful",
				"xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
				"brm:POID":"0.0.0.1 /account 371928 0","brm:BUSINESS_TYPE":30000000,
				"brm:ACCOUNT_NO":1523168129897,"brm:STATUS":0}}*/
			JSONObject object = new JSONObject(result);
			JSONArray serviceArray=null;
			object = object.optJSONObject("brm:COB_OP_CUST_CREATE_COMPANY_outputFlist");
			JSONObject billInfoObject=null;
			String commisionModel=null;
			String settlementPoId=null;
			String returnValue=null;
			serviceArray = object.optJSONArray("brm:SERVICES");
			if(serviceArray !=null){
				for(int i=0;i<serviceArray.length();i++){
					settlementPoId=serviceArray.optString(i, "brm:SERVICE_OBJ");
					
					if(settlementPoId !=null){
						String[] args = settlementPoId.split(" ");
						commisionModel = args[1];
						System.out.println(commisionModel);
					}
					
					if(commisionModel.equalsIgnoreCase("/service/settlement/prepaid")){
						 returnValue = settlementPoId;
						if(returnValue !=null){
							String[] args = returnValue.split(" ");
							returnValue = args[2];
							System.out.println(returnValue);
						}
						return returnValue;	
					}else if(commisionModel.equalsIgnoreCase("/service/settlement/postpaid")){
						 returnValue  = settlementPoId;
						if(returnValue !=null){
							String[] args = returnValue.split(" ");
							returnValue = args[2];
							System.out.println(returnValue);
						}
						return returnValue;	
					}else{
						return null;
					}
					
				}
			}
			
			/*if(serviceArray !=null){
				billInfoObject=object.optJSONObject("brm:SERVICES");
				settlementPoId=billInfoObject.optString("brm:SERVICE_OBJ");
			}*/
			//String returnValue = returnValues;
			/*if(returnValue !=null){
				String[] args = returnValue.split(" ");
				returnValue = args[2];
				System.out.println(returnValue);
			}*/
			return null;	
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	private Set<String> retrivePurchaseProductPoidForCreateAgreement(String result) {
		Set<String> substances= new HashSet<String>();
		JSONArray plansArray=null;JSONObject planObject = null;
		try{
			JSONObject resultJsonObject = new JSONObject(result);
			resultJsonObject = resultJsonObject.optJSONObject("brm:COB_OP_CUST_ADD_PARTNER_AGREEMENT_outputFlist");
			JSONObject planListCode=resultJsonObject.optJSONObject("brm:PLAN_LIST_CODE");
			plansArray = planListCode.optJSONArray("brm:PLAN");
			if(plansArray==null){
				plansArray = new JSONArray("["+planListCode.optJSONObject("brm:PLAN")+"]");
			}
			for(int i=0;i<plansArray.length();i++){
				planObject=plansArray.getJSONObject(i);
				String orderNo =  String.valueOf(planObject.optLong("brm:PACKAGE_ID"));
				String productPoId = this.retrivePoId(planObject.optString("brm:PRODUCT_OBJ"));
				String purchaseProductPoId = this.retrivePoId(planObject.optString("brm:OFFERING_OBJ"));
				String planPoId = this.retrivePoId(planObject.optString("brm:PLAN_OBJ"));
				System.out.println("substanc is : "+orderNo+"_"+planPoId+"_"+ productPoId +"_"+purchaseProductPoId);
				substances.add(orderNo+"_"+planPoId+"_"+ productPoId +"_"+purchaseProductPoId);
			}
			return substances;
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
		@Override
	public CommandProcessingResult syncplan(JsonCommand jsonCommand) {
		
		final JsonArray planArray=jsonCommand.arrayOfParameterNamed("plans");
		int j=0;
		String[] plans = new String[planArray.size()];
		if(planArray.size() > 0){
			for(int i = 0; i < planArray.size(); i++){
				plans[i] = planArray.get(i).toString();
			}
			for(String plan:plans){
				final JsonElement planElement = this.fromApiJsonHelper.parse(plan);
				Plan planNGB=this.assembleDetails(jsonCommand,planElement);
				j++;
			}	
		}
		
		return new CommandProcessingResultBuilder() 
	    .withCommandId(jsonCommand.commandId()) 
	    .withOfficeId(null) 
	    .withClientId(null)
	    .withResourceIdAsString(new Integer(j).toString())
	    .withGroupId(null) 
	    .withEntityId(null) 
	    .build();
	}
		
		
	@Override
	public CommandProcessingResult createBillPlan(ProvisioningRequest provisioningRequest) {
		String resourceId =null;
	       
	        BillPlanData billPlanData = this.provisioningReadPlatformService.retriveBillPlan(provisioningRequest);
	      //  String logedInUser= context.authenticatedUser().getUsername();
	       String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.BILLPLAN_OPCODE,billPlanData.celcomRequestInput());
	       return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).build();
	}

		
	@Override
	public CommandProcessingResult updatePurchaseProductPoId(Order order, Set<String> substances) {
		List<OrderLine> orderlines = order.getServices();
		for(OrderLine orderLine:orderlines){
			for(String substance:substances){
				if((this.getValueFromSubstance(substance,0)).equalsIgnoreCase(order.getOrderNo()) &&(this.getValueFromSubstance(substance,2)).equalsIgnoreCase(String.valueOf(orderLine.getProductPoId()))){
					orderLine.setPurchaseProductPoId(Long.valueOf(this.getValueFromSubstance(substance,3)));
					break;
				}
			}
			this.orderLineRepository.saveAndFlush(orderLine);
		}
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}

	private String getValueFromSubstance(String value, int i) {
		String arr[] = value.split("_");
		return arr[i];
	}
	
	@Override
	public CommandProcessingResult addPlan(String inputJson) {
		//
		String clientServicePoid=null;
		String clientPoid= null;
		Set<String> substances=null;
		OrderData orderData=OrderData.fromJsonToAddplan(inputJson);
		 String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.ADD_PLAN,orderData.celcomRequestInput(logedInUser));
		if(result !=null){
			clientServicePoid=this.retriveClientServicePoid(result);
			clientPoid=this.retriveClientPoid(result);
			substances=this.retrivePurchaseProductPoid(result);
		}
			
		return new CommandProcessingResultBuilder().
				withResourceIdAsString(clientServicePoid).
				withClientId(Long.parseLong(clientPoid)).
				withSubstances(substances).build();
	}
		
		
		
	@Override
	public CommandProcessingResult cancelPlan(String json) {
		//
		Set<String> substances=null;
		OrderData orderData=OrderData.fromJsonToCancelplan(json);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CANCEL_PLAN,orderData.celcomRequestInputForCancelPlan(logedInUser));
		
		return new CommandProcessingResultBuilder().
			withSubstances(substances).build();
	}


	@Override
	public CommandProcessingResult suspendClientService(JsonCommand command) {
		
		ClientServiceData data = ClientServiceData.fromJsonToSuspend(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.SUSPEND_CLIENTSERVICE_OPCODE,data.celcomRequestInputForSuspend(logedInUser));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}

		
	@Override
	public CommandProcessingResult terminateClientService(JsonCommand command) {
		
		ClientServiceData data = ClientServiceData.fromJsonToSuspend(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.TERMINATE_CLIENTSERVICE_OPCODE,data.celcomRequestInputForTerminate(logedInUser));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}
		
		
		
	@Override
	public CommandProcessingResult reactivateClientService(JsonCommand command) {
		
		ClientServiceData data = ClientServiceData.fromJsonToSuspend(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.REACVTIVATE_CLIENTSERVICE_OPCODE,data.celcomRequestInputForReActivate(logedInUser));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}
		
		@Override
		public CommandProcessingResult createPayment(JsonCommand command) {
			
			PaymentData data = PaymentData.fromJsonToPaymentsCelcom(command);
			String logedInUser= context.authenticatedUser().getUsername();
			String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.PAYMENTS_OPCODE,data.celcomRequestInputForPaymentsCelcome(logedInUser));
			String receiptNo=null;
			if(result !=null){
				receiptNo = this.retriveReceiptNoforPayments(result);
				
			}
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(receiptNo).build();
				
		}
		
	

		private String retriveReceiptNoforPayments(String result) {
		
			try{

				JSONObject object = new JSONObject(result);
				object = object.optJSONObject("brm:COB_OP_PYMT_COLLECT_PAYMENT_outputFlist");
				String returnValue = object.optString("brm:RECEIPT_NO");
				return returnValue;	
				
			}catch(JSONException e){
				throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
			}
		
		}		
		
		
		
		
		
		
		

		@Override
		public CommandProcessingResult createAdjustmentsCelcom(JsonCommand command) {
			
			AdjustmentData data = AdjustmentData.fromJsonToAdjustmentsCelcom(command);
			String logedInUser= context.authenticatedUser().getUsername();
			String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.ADJUSTMENTS_OPCODE,data.celcomRequestInputForADJUSTMENTSCelcom(logedInUser));
			return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
				
		}
		
		

		
		
	@Override
	public CommandProcessingResult addPlans(JsonCommand command) {
		String clientServicePoId=null;
		String clientPoId=null;
		Set<String> substances = null;
		List<OrderData> multiplePlans = this.fromJsonToMultipleplans(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String orderNo = this.retriveOrderNo(command.getClientId());
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.ADD_PLAN,OrderData.celcomRequestInputForAddPlans(multiplePlans,logedInUser,orderNo));
		if(result !=null){
			clientServicePoId=this.retriveClientServicePoid(result);
			clientPoId=this.retriveClientPoid(result);
			substances=this.retrivePurchaseProductPoid(result);
		}
			
		return new CommandProcessingResultBuilder().
				withResourceIdAsString(clientServicePoId).
				withClientId(Long.parseLong(clientPoId)).
				withSubstances(substances).build();
	}

	
	@Override
	public CommandProcessingResult cancelPlans(JsonCommand command) {
		
		List<OrderData> multiplePlans = this.fromJsonToMultipleplans(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CANCEL_PLAN,OrderData.celcomRequestInputForCancelPlans(multiplePlans,logedInUser));
		return new CommandProcessingResultBuilder().build();
	}	
		
	@Override
	public CommandProcessingResult renewalplan(JsonCommand command) {
		OrderData data = OrderData.fromJsonToRenewalPlan(command);

		String logedInUser= this.context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.RENEWAL_PLAN,data.celcomRequestInputForRenewalPlan(logedInUser));
		return new CommandProcessingResultBuilder().build();
	}		
	@Override
	public CommandProcessingResult changePlan(String json) {
		Set<String> substances=null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		OrderData orderData=OrderData.fromJsonToChangePlan(json);
		String clientId = jsonObject.optString("clientId");
		String order = this.retriveOrderNo(Long.valueOf(clientId));
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CHANGE_PLAN,orderData.celcomRequestInputForChangePlan(logedInUser,order));
		if(result !=null){
			substances=this.retrivePurchaseProductPoidForChangePlan(result);
		}
		return new CommandProcessingResultBuilder().
				withSubstances(substances).build();
	}	
	
	@Override
	public CommandProcessingResult createCelcomAgreement(JsonCommand command) {
		Set<String> substances=null;
		Long clientId = null;
		List<AgreementData> datas = this.fromJsonToCreateAgreement(command);
		for(AgreementData data:datas){
		      clientId = data.getClientId();
		}
		String order = this.retriveOrderNo(clientId);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CREATE_AGREEMENT,AgreementData.celcomRequestInputForCreateAgreement(datas,logedInUser,order));
		if(result !=null){
			substances=this.retrivePurchaseProductPoidForCreateAgreement(result);
		}
		return new CommandProcessingResultBuilder().withSubstances(substances).build();
	}
	
		
	
	  public CommandProcessingResult updateCelcomClient(final Long clientId, final JsonCommand command) {
		
		  final Client clientForUpdate = this.clientRepository.findOneWithNotFoundDetection(clientId);
		  final Map<String, Object> changes = clientForUpdate.update(command);
		  
		  final List<AddressData> addressDatas =this.addressReadPlatformService.retrieveClientAddressDetails(clientId);
		  AddressData primaryAddressData= addressDatas.get(0);
          AddressData billingAddressData=addressDatas.get(1);
          
          
		  final Address  newAddress=Address.fromJson(clientId, command);
		  String officePoId= command.stringValueOfParameterName("officePoId");
		  String logedInUser= context.authenticatedUser().getUsername();
		  boolean lcoMovement = command.booleanPrimitiveValueOfParameterNamed("lcoMovement"); 
          String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UPDATECLIENT_OPCODE,
        		  clientForUpdate.celcomRequestInputForCLIENTSCelcom(logedInUser,newAddress,primaryAddressData,billingAddressData,officePoId));
          String resultLcoMovement=null;
          if(lcoMovement==true){
        	 resultLcoMovement = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.LCOMOVEMENT_OPCODE,
            		  clientForUpdate.celcomRequestInputForLCOMovement(logedInUser,officePoId));
    		    
          }
          
		  return null;
	  
	    }
	  
	  
	  
	  
	  @Override
		public CommandProcessingResult createOfficePayment(JsonCommand command) {
				
		    OfficePaymentData data = OfficePaymentData.fromJsonToCelcomOfficePayment(command);
		    data.setWallet(command.booleanPrimitiveValueOfParameterNamed("isWallet"));
		    String logedInUser= context.authenticatedUser().getUsername();
		    String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.OFFICEPAYMENTS_OPCODE,data.celcomRequestInputForOfficePaymentsCelcom(logedInUser));
		    return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
			
		}
	  
	  
	  @Override
		public CommandProcessingResult createOfficeAdjustmentsCelcom(JsonCommand command) {
			
		    OfficeAdjustmentData data = OfficeAdjustmentData.fromJsonToOfficeAdjustmentsCelcom(command);
			String logedInUser= context.authenticatedUser().getUsername();
			String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.OFFICEADJUSTMENTS_OPCODE,data.celcomRequestInputForOfficeAdjustmentsCelcom(logedInUser));
			return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
				
		}
	  
	  
	  @Override
		public CommandProcessingResult cancelPayment(JsonCommand command) {
				
		    PaymentData data = PaymentData.fromJsonToCancelPaymentsCelcom(command);
		    String logedInUser= context.authenticatedUser().getUsername();
		    String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CANCELPAYMENTS_OPCODE,data.celcomRequestInputForCancelPayment(logedInUser));
		    return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
			
		}
	  @Override
		public CommandProcessingResult cancelPaymentforOffice(JsonCommand command) {
				
		  OfficePaymentData data = OfficePaymentData.fromJsonToCancelPaymentsOfficeCelcom(command);
		  String logedInUser= context.authenticatedUser().getUsername();
		  String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CANCELPAYMENTSOFFICE_OPCODE,data.celcomRequestInputForOfficeCancelPayment(logedInUser));
		  return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
			
		}
	  
	  
	  
	  
	 	
	
	
	@Override
	public CommandProcessingResult swapDevice(JsonCommand command) {
		ItemMasterData itemMasterData=this.extractSwapDeviceJson(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.SWAP_DEVICE,itemMasterData.celcomRequestInputForSwapDevice(logedInUser));
		
	          // resourceId = this.retriveClientServicePoid(result);
	       
		return new CommandProcessingResultBuilder().build();
	}	
	
	@Override
	public CommandProcessingResult updateOffice(JsonCommand command) {
		try {
            Long parentId = null;
            if (command.parameterExists("parentId")) {
                parentId = command.longValueOfParameterNamed("parentId");
            }
            String officeMovement=null;
            String url = command.getUrl();
            String[] urlArrayString= url.split("/");
    		Long officeId=Long.parseLong(urlArrayString[2]);
            final Office office = this.officeRepository.findOne(officeId);
            final Map<String, Object> changes = office.update(command);

            if (changes.containsKey("parentId")) {
            	Office oldParentOffice=office.getParent();
                String oldParentOfficeType=oldParentOffice.getOfficeType();
            	final Office parent = this.officeRepository.findOne(parentId);
                String newParentOfficeType=parent.getOfficeType();
                officeMovement=oldParentOfficeType+"_"+newParentOfficeType;
            	office.update(parent);
            	
            	switch (officeMovement) {
				case "MSO_DIST":
					officeMovement="1";
					break;

				case "DIST_MSO":
					officeMovement="2";
					break;
				case "DIST_DIST":
					officeMovement="3";
					break;
				
				default:
					officeMovement=null;
					break;
				}
            }
            
            //update officeAddress
            final  OfficeAddress officeAddress  = this.addressRepository.findOneWithPartnerId(office);
            final Map<String, Object> addressChanges = officeAddress.update(command);
            
            
            if(!addressChanges.isEmpty()){
            	office.setOfficeAddress(officeAddress);
 		    }

            String logedInUser= context.authenticatedUser().getUsername();
    		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UPDATEOFFICE_OPCODE,office.celcomRequestInputUpdateCrm(logedInUser, 
    				changes,addressChanges,officeMovement));
    		
            return new CommandProcessingResultBuilder() //
                   .build();
        } catch (DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
	}

	@Override
	public CommandProcessingResult createClientHardwarePlanActivation(JsonCommand command) {
		String resourceId =null;
	    Set<String> substances = null;

        ClientServiceData clientServiceData = ClientServiceData.fromJsonForCelcom(command);
        String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.CREATECLIENTSIMPLEACTIVATION_OPCODE,clientServiceData.celcomRequestInputForPlanActivation(logedInUser));
		if(result !=null){
			resourceId = this.retriveClientServicePoid(result);
			substances = this.retrivePurchaseProductPoid(result);
		}
			
		return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).withSubstances(substances).build();
}
	
	
	@Override
	public CommandProcessingResult createClientDiscount(JsonCommand command) {
		//
		Set<String> substances=null;
		ClientDiscount clientDiscountData=ClientDiscount.formJson(command);
		clientDiscountData.setClient(this.clientRepository.findOneWithNotFoundDetection(command.longValueOfParameterNamed("clientId")));
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UPDATECLIENT_OPCODE,clientDiscountData.celcomRequestInputForClientDiscount(logedInUser));
		
		return new CommandProcessingResultBuilder().
			withSubstances(substances).build();
	}
	
	
	
	
	
	
	
	
	
	
	
	private ItemMasterData extractSwapDeviceJson(JsonCommand command) {
		String newSTBno=null;
		String newScno=null;
		String clientPoId=command.stringValueOfParameterName("clientPoId");
		String clientServicePoId=command.stringValueOfParameterName("clientServicePoId");
		String type=command.stringValueOfParameterName("type");
		if(type.equalsIgnoreCase("STB")){
			newSTBno=command.stringValueOfParameterName("newSerialNo");
			JsonArray pairableDevicesArray= command.arrayOfParameterNamed("pairableItemDetails").getAsJsonArray();
			if(!pairableDevicesArray.isJsonNull()&& pairableDevicesArray.size()>0){
				JsonElement pairableDevicesElement=pairableDevicesArray.get(0);
				if(!pairableDevicesElement.isJsonNull()){
					newScno=this.fromApiJsonHelper.extractStringNamed("newSerialNo", pairableDevicesElement);
				}
			}
		}else if (type.equalsIgnoreCase("SC")) {
			newScno=command.stringValueOfParameterName("newSerialNo");
		}
		return new ItemMasterData(clientPoId, clientServicePoId, newSTBno, newScno);
	}
	

	@Override
	public CommandProcessingResult deleteAgreement(JsonCommand command) {
		AgreementData data = AgreementData.fromJsonToDeleteAgreement(command);
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.DELETE_AGREEMENT,data.celcomRequestInputForDeleteAgreement());
		return new CommandProcessingResultBuilder().build();
	}

		private Plan assembleDetails(final JsonCommand command,JsonElement planElement) {
			Map<Long,Long> mapPoidToId=new HashMap<Long,Long>();
			final Set<PlanDetails> allProducts = new HashSet<>();
			List<Price> priceList= new ArrayList<Price>();
			String[]  products = null;
			final String planCode = this.fromApiJsonHelper.extractStringNamed("planCode", planElement);
			final String planDescription = this.fromApiJsonHelper.extractStringNamed("planDescription", planElement);
			LocalDate startDate = this.fromApiJsonHelper.extractLocalDateNamed("startDate", planElement);
			if(startDate==null){
				startDate= new LocalDate();
			}
			final LocalDate endDate = this.fromApiJsonHelper.extractLocalDateNamed("endDate", planElement);
			final Long status = this.fromApiJsonHelper.extractLongNamed("status", planElement);
			final Long billRule =(long) 300;
			final String provisioingSystem="Provision";
			boolean isPrepaid=false;
			if(this.fromApiJsonHelper.parameterExists("isPrepaid", planElement)){
				final String prepaidString=this.fromApiJsonHelper.extractStringNamed("isPrepaid", planElement);
				if(prepaidString.equals("Y")){
					isPrepaid=true;
				}
					
			}
			final boolean allowTopup=false;
			final boolean isHwReq=false;
			final Long planType=(long) 210;
			final String currencyId = new Integer(356).toString();
			final Long poid=this.fromApiJsonHelper.extractLongNamed("planPoid", planElement);
			
			Plan planCRM =new Plan(planCode,planDescription,startDate,endDate,billRule,status,null,
					provisioingSystem,isPrepaid,allowTopup,isHwReq,planType,currencyId,poid);
			
			
		
			final JsonArray productsArray=this.fromApiJsonHelper.extractJsonArrayNamed("productsDatas", planElement);
			products = new String[productsArray.size()];
			if(productsArray.size() > 0){
				for(int i = 0; i < productsArray.size(); i++){
					products[i] = productsArray.get(i).toString();
				}
				for(int i=0;i<products.length;i++){
					final JsonElement productElement = this.fromApiJsonHelper.parse(products[i]);
					final Long productPoId= this.fromApiJsonHelper.extractLongNamed("productPoid", productElement);
					final String productDescription=this.fromApiJsonHelper.extractStringNamed("productDescription", productElement);
					final String productCode=this.fromApiJsonHelper.extractStringNamed("productCode", productElement);
					final long priorityLong=this.fromApiJsonHelper.extractLongNamed("priority", productElement);
					final int priority=(int) priorityLong;
					final String productCategory="P";
					List<ServiceMaster> serviceList=this.serviceMasterRepository.findServiceIdUsingserviceType("CATV");
					Long serviceId=null;
					if(serviceList.isEmpty()){
						throw new PlatformApiDataValidationException("Services Are Not Present, Please Create the Services 'CATV'", "", null);
					}
					for(ServiceMaster service:serviceList){
						serviceId=service.getId();
						break;
					}
					
					final String statusProduct="ACTIVE";
			
					Product productCRM = new Product(productCode, productDescription, 
							productCategory, serviceId, statusProduct, productPoId, priority);
					Product productNGB=this.productRepository.findOneByProductPoid(productPoId);
					Long productId=null;
					if(productNGB==null){
						this.productRepository.saveAndFlush(productCRM);
						productId=productCRM.getId();
						mapPoidToId.put(productCRM.getProductPoid(), productCRM.getId());
					}else{
						productNGB.update(productCRM);
						this.productRepository.save(productNGB);
						productId=productNGB.getId();
						mapPoidToId.put(productNGB.getProductPoid(), productNGB.getId());
					}
					
					final BigDecimal price =this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("price", productElement);
					final String chargeCode = this.fromApiJsonHelper.extractStringNamed("chargeCode", productElement);
					final String contractType = this.fromApiJsonHelper.extractStringNamed("contractPeriod", productElement);
					final String chargeOwner = this.fromApiJsonHelper.extractStringNamed("chargeOwner", productElement);
					Price priceCRM = new Price(price,null,productId, chargeCode, contractType, chargeOwner);
					priceList.add(priceCRM);
				}
				final JsonArray dealsArray=this.fromApiJsonHelper.extractJsonArrayNamed("planDetailData", planElement);
				String[] deals = new String[dealsArray.size()];
				if(dealsArray.size() > 0){
					for(int i = 0; i < dealsArray.size(); i++){
						deals[i] = dealsArray.get(i).toString();
					}
				for(String deal:deals){
					final JsonElement dealElement = this.fromApiJsonHelper.parse(deal);
					final Long dealPoid=this.fromApiJsonHelper.extractLongNamed("dealPoId", dealElement);
					final Long productPoid=this.fromApiJsonHelper.extractLongNamed("productPoId", dealElement);
					final Long productId=mapPoidToId.get(productPoid);
					PlanDetails planDetailsCRM=new PlanDetails(productId, null,dealPoid);
					allProducts.add(planDetailsCRM);
				}
			}
				
			planCRM.addProductDetails(allProducts);
			Plan planNGB=this.planRepository.findwithPlanPoid(planCRM.getPlanPoid());
			Long planId;
			if(planNGB==null){
				this.planRepository.save(planCRM);
				planId=planCRM.getId();
			}else{
				planNGB.update(planCRM);
				planNGB.addProductDetails(allProducts);
				this.planRepository.save(planNGB);
				planId=planNGB.getId();
			}
			
			for(Price priceCRM:priceList){
				priceCRM.setPlanCode(planId);
				List<Price> priceListNGB=this.priceRepository.findOneByPlanAndProduct(planId, priceCRM.getProductId());
				if(priceListNGB.size()==0){
					this.priceRepository.save(priceCRM);
				}
				else{
					for(Price priceNGB:priceListNGB){
						priceNGB.update(priceCRM);
					}
					this.priceRepository.save(priceListNGB);
				}
			}
			if(planNGB==null){
				return planCRM;
			}else{
				return planNGB;
			}		
		}
		return planCRM;
	}
		

		private String retriveAccountNo(String result) {
			try{
				JSONObject object = new JSONObject(result);
				object = object.optJSONObject("brm:COB_OP_CUST_CREATE_SUBSCRIBER_outputFlist");
				return object.optString("brm:ACCOUNT_NO");
				
				
			}catch(JSONException e){
				throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
			}
		}
		
		private String accountNumberPriRequirValue() {
			final Collection<MCodeData> mcodeData = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.NUMBER_GENERATOR);
			String priValue=null;
			for(MCodeData data:mcodeData){
	    	   if(data.getOrderPossition() == 1){
	    		   priValue = data.getmCodeValue();
	    	        break;
	    	   }
		    }
			if(priValue == null){
				throw new PlatformDataIntegrityException("error.msg.number.generatsor.not.available","","","");
			}
			return priValue;
		}
		
		
	private List<OrderData> fromJsonToMultipleplans(JsonCommand command) {
		OrderData orderData= null;
		List<OrderData> plans=new ArrayList<OrderData>();
		final JsonArray multiplePlans = command.arrayOfParameterNamed("plans").getAsJsonArray();
		for(JsonElement planElement : multiplePlans){
			String clientPoId=this.fromApiJsonHelper.extractStringNamed("clientPoId", planElement);
			String clientServicePoId=this.fromApiJsonHelper.extractStringNamed("clientServicePoId", planElement);
			String planPoId=this.fromApiJsonHelper.extractStringNamed("planPoId", planElement);
			String dealPoId=this.fromApiJsonHelper.extractStringNamed("dealPoId", planElement);
			String orderNo=this.fromApiJsonHelper.extractStringNamed("orderNo", planElement);
			orderData=new OrderData(clientPoId, clientServicePoId, planPoId, dealPoId, orderNo);
			plans.add(orderData);
		}
		return plans;
	}

	
	private Address extractAddressForCelcom(Long clientId,JsonElement addressElement) {
			String addressKey = this.fromApiJsonHelper.extractStringNamed("addressType", addressElement);
			addressKey=addressKey.isEmpty()?"PRIMARY":addressKey;
		    final String addressNo = this.fromApiJsonHelper.extractStringNamed("addressNo",addressElement);
		    final String street = this.fromApiJsonHelper.extractStringNamed("street",addressElement);
		    final String city = this.fromApiJsonHelper.extractStringNamed("city",addressElement);
		    final String zip = this.fromApiJsonHelper.extractStringNamed("zipCode",addressElement);
		    final String state = this.fromApiJsonHelper.extractStringNamed("state",addressElement);
		    final String country = this.fromApiJsonHelper.extractStringNamed("country",addressElement);
		    final String phone = this.fromApiJsonHelper.extractStringNamed("phone",addressElement);
		    final String email = this.fromApiJsonHelper.extractStringNamed("email",addressElement);
		    final String district = this.fromApiJsonHelper.extractStringNamed("district",addressElement);
		    return new Address(clientId, addressKey, addressNo, street, city, state, country, zip,phone,email,district);
	
	}
	
	private String findError(JSONObject result) {
		String error=null;
		String errorCode=result.optString("brm:ERROR_CODE");
		if(!("".equals(errorCode))){
			error=result.optString("brm:ERROR_DESCR");
		}
	return error;
	}


	/**
	 * To update the credit limit in BRM.
	 */
	public CommandProcessingResult updateCreditLimit(JsonCommand command, OfficeBalance officeBalance) {
		String resourceId = null;
		String transactionId = null;
		String loggedInUser= context.authenticatedUser().getUsername();

		//OfficeBalance officeBalance = OfficeBalance.officeCreditLimitFromJson(command);
        final Office office = this.officeRepository.findOne(officeBalance.getofficeId());
        String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UPDATE_CREDIT_LIMIT_OPCODE, office.buildCreditLimitUpdateRequest(officeBalance.getCreditLimit(), loggedInUser));
		if(result !=null){
			resourceId = this.retriveClientPoidforOfficeCreditLimit(result);
			//transactionId=this.retriveSettlementPoidforOffice(result);
		}
		return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).withTransactionId(transactionId).build();
	}

// 
	
	private String retriveClientPoidforOfficeCreditLimit(String result) {
		try{
			/*{"brm:COB_OP_CUST_SET_CREDIT_LIMIT_outputFlist":{"brm:CREDIT_LIMIT":125,
			  "brm:SERVICE_OBJ":"0.0.0.1 /service/settlement/prepaid 809853 0",
			  "brm:DESCR":"Credit Limit Updated Sucessfully",
			  "xmlns:brm":"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes",
			  "brm:POID":"0.0.0.1 /account 808381 0","brm:STATUS":0}}*/
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:COB_OP_CUST_SET_CREDIT_LIMIT_outputFlist");
			String returnValue = object.optString("brm:POID");
			if(returnValue !=null){
				String[] args = returnValue.split(" ");
				returnValue = args[2];
				System.out.println(returnValue);
			}else{
				throw new PlatformDataIntegrityException("poid.invalid","","","");	
			}
			return returnValue;	
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}

	@Override
	public CommandProcessingResult createBillAdjustmentsCelcom(JsonCommand command) {
		AdjustmentData data = AdjustmentData.fromJsonToBillAdjustmentsCelcom(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.BILL_ADJUSTMENT_OPCODE,data.celcomRequestInputForBILLADJUSTMENTSCelcom(logedInUser));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}
	@Override
	public CommandProcessingResult BroadcasterConfigCelcom(JsonCommand command) {
		BroadcasterData data = BroadcasterData.fromJsonToBroadcasterConfigCelcom(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UTILS_BCAST_CONFIG_OPCODE,data.celcomRequestInputForBROADCASTERCONFIGCelcom(logedInUser));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}
	@Override
	public CommandProcessingResult ChannelConfigCelcom(JsonCommand command) {
		ChannelData data = ChannelData.fromJsonToChannelConfigCelcom(command);
		String logedInUser= context.authenticatedUser().getUsername();
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UTILS_BCAST_CONFIG_OPCODE,data.celcomRequestInputForCHANNELCONFIGCelcom(logedInUser));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}
	@Override
	public CommandProcessingResult ProductConfigCelcom(Long productId) {
		List<ChannelMappingData> channelMappingList = this.channelMappingReadPlatformService.retriveproductmapping(productId);
		String logedInUser= context.authenticatedUser().getUsername();
		ChannelMappingData channelMappingData = channelMappingList.get(0);
		String result = this.celcomReadWriteConsiliatrService.processCelcomRequest(CelcomApiConstants.UTILSS_BCASTS_CONFIG_OPCODE,channelMappingData.celcomRequestInputForPRODUCTCONFIGCelcom(logedInUser, channelMappingList));
		return new CommandProcessingResultBuilder().withResourceIdAsString("success").build();
	}
	
public List<AgreementData> fromJsonToCreateAgreement(JsonCommand command) {
	    
	    AgreementData agreementData= null;
		List<AgreementData> plans=new ArrayList<AgreementData>();
		final JsonArray multiplePlans = command.arrayOfParameterNamed("plans").getAsJsonArray();
		for(JsonElement planElement : multiplePlans){
			String poId=this.fromApiJsonHelper.extractStringNamed("poId", planElement);
			String planPoId=this.fromApiJsonHelper.extractStringNamed("planPoId", planElement);
			String dealPoId=this.fromApiJsonHelper.extractStringNamed("dealPoId", planElement);
			String settlementPoId=this.fromApiJsonHelper.extractStringNamed("settlementPoId", planElement);
			Long clientId =this.fromApiJsonHelper.extractLongNamed("clientId", planElement);
			agreementData=new AgreementData(poId, planPoId, dealPoId, settlementPoId,null);
			agreementData.setClientId(clientId);
			plans.add(agreementData);
		}
		return plans;
	
	}

	

}
	

