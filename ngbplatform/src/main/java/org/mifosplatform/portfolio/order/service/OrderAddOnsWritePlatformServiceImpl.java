package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.planprice.exceptions.ChargeCodeAndContractPeriodException;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderWritePlatformService;
import org.mifosplatform.finance.chargeorder.service.GenerateChargesForOrderService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.allocation.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderAddons;
import org.mifosplatform.portfolio.order.domain.OrderAddonsRepository;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderPriceRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.exceptions.AddonEndDateValidationException;
import org.mifosplatform.portfolio.order.serialization.OrderAddOnsCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMapping;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMappingRepository;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


@Service
public class OrderAddOnsWritePlatformServiceImpl implements OrderAddOnsWritePlatformService{
	
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final ServiceMappingRepository serviceMappingRepository;
	private final OrderAddOnsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ContractRepository contractRepository;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;
	private final OrderAssembler orderAssembler;
	private final OrderRepository orderRepository;
	private final OrderPriceRepository orderPriceRepository;
	private final HardwareAssociationRepository hardwareAssociationRepository;
	private final OrderAddonsRepository addonsRepository;
	private final GenerateChargesForOrderService generateChargesForOrderService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final PlanRepository planRepository;
	private final ChargeCodeRepository chargeCodeRepository;
	private final ClientRepositoryWrapper clientRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	
@Autowired
 public OrderAddOnsWritePlatformServiceImpl(final PlatformSecurityContext context,final OrderAddOnsCommandFromApiJsonDeserializer fromApiJsonDeserializer,
		 final FromJsonHelper fromJsonHelper,final ContractRepository contractRepository,final OrderAssembler orderAssembler,final OrderRepository orderRepository,
		 final ServiceMappingRepository serviceMappingRepository,final OrderAddonsRepository addonsRepository,final PlanRepository  planRepository,
		 final ProvisioningWritePlatformService provisioningWritePlatformService,final HardwareAssociationRepository associationRepository,
		 final OrderPriceRepository orderPriceRepository,final GenerateChargesForOrderService generateChargesForOrderService,
		 final ChargingOrderWritePlatformService chargingOrderWritePlatformService,final ChargeCodeRepository chargeCodeRepository,
		 final ClientRepositoryWrapper clientRepository,final OrderReadPlatformService orderReadPlatformService){
		
	this.context=context;
	this.fromJsonHelper=fromJsonHelper;
	this.fromApiJsonDeserializer=fromApiJsonDeserializer;
	this.contractRepository=contractRepository;
	this.orderRepository=orderRepository;
	this.provisioningWritePlatformService=provisioningWritePlatformService;
	this.orderPriceRepository=orderPriceRepository;
	this.orderAssembler=orderAssembler;
	this.hardwareAssociationRepository=associationRepository;
	this.planRepository = planRepository;
	this.addonsRepository=addonsRepository;
	this.serviceMappingRepository=serviceMappingRepository;
	this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
	this.generateChargesForOrderService = generateChargesForOrderService;
	this.chargeCodeRepository = chargeCodeRepository;
	this.clientRepository = clientRepository;
	this.orderReadPlatformService = orderReadPlatformService;
}


@Override
public CommandProcessingResult createOrderAddons(JsonCommand command,Long orderId) {
	
	try{
		
		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		final JsonElement element = fromJsonHelper.parse(command.json());
		final JsonArray addonServices = fromJsonHelper.extractJsonArrayNamed("addonServices", element);
		final String planName=command.stringValueOfParameterNamed("planName");
		final Long contractId=command.longValueOfParameterNamed("contractId");
		final LocalDate startDate=command.localDateValueOfParameterNamed("startDate");
	    Order order=this.orderRepository.findOne(orderId);
	    Contract contract=this.contractRepository.findOne(contractId);
	    Date addonEndDate = null;
	    LocalDate endDate = this.orderAssembler.calculateEndDate(new LocalDate(startDate),
                contract.getSubscriptionType(), contract.getUnits());
	    if(endDate == null && order.getEndDate() != null){
	    	throw new AddonEndDateValidationException(orderId);
	    }
	    if(order.getEndDate() != null && endDate.isAfter(new LocalDate(order.getEndDate()))){
           throw new AddonEndDateValidationException(orderId);
	    //	endDate = new LocalDate(order.getEndDate());
	      }
	    
	    
	   
	    Client client=this.clientRepository.findOneWithNotFoundDetection(order.getClientId());
	    HardwareAssociation association=this.hardwareAssociationRepository.findOneByOrderId(orderId);
		for (JsonElement jsonElement : addonServices) {
			OrderAddons addons=assembleOrderAddons(jsonElement,fromJsonHelper,order,startDate,endDate,contractId);
			this.addonsRepository.saveAndFlush(addons);
			
			if(!"None".equalsIgnoreCase(addons.getProvisionSystem())){
				
				this.provisioningWritePlatformService.postOrderDetailsForProvisioning(order, planName, UserActionStatusTypeEnum.ADDON_ACTIVATION.toString(),
						Long.valueOf(0), null,association!=null?association.getSerialNo():null,orderId, addons.getProvisionSystem(),addons.getId());
			}
		OrderPrice orderPrice =this.orderPriceRepository.findOne(addons.getPriceId());
		List<BillingOrderData> billingOrderDatas = new ArrayList<BillingOrderData>(); 
		 if(endDate != null){ addonEndDate = endDate.toDate();}
		 else{addonEndDate = startDate.plusYears(100).toDate();}
		
		//if(order.getNextBillableDay() != null){
			
			billingOrderDatas.add(new BillingOrderData(orderId,addons.getPriceId(),order.getPlanId(),order.getClientId(),startDate.toDate(),
					orderPrice.getNextBillableDay(),addonEndDate,"",orderPrice.getChargeCode(),orderPrice.getChargeType(),Integer.valueOf(orderPrice.getChargeDuration()),
					orderPrice.getDurationType(),orderPrice.getInvoiceTillDate(),orderPrice.getPrice(),"N",orderPrice.getBillStartDate(),addonEndDate,
					order.getStatus(),orderPrice.isTaxInclusive()?1:0,String.valueOf(client.getTaxExemption()),false,orderPrice.getCurrencyId(),orderPrice.getChargeOwner()));
			
			List<ChargeData> billingOrderCommands = this.generateChargesForOrderService.generatebillingOrder(billingOrderDatas);
			List<BillItem> invoiceList = this.generateChargesForOrderService.generateCharge(billingOrderCommands);
			
			
			List<OrderData> orderData = this.orderReadPlatformService.orderDetailsForClientBalance(orderId);
			for(BillItem invoice:invoiceList)
			{
			JsonObject clientBalanceObject = new JsonObject();
				clientBalanceObject.addProperty("clientId",orderData.get(0).getClientId());
				clientBalanceObject.addProperty("amount", invoice.getInvoiceAmount());
				clientBalanceObject.addProperty("isWalletEnable", false);
				clientBalanceObject.addProperty("clientServiceId",orderData.get(0).getClientServiceId());
				clientBalanceObject.addProperty("currencyId",orderData.get(0).getCurrencyId());
				clientBalanceObject.addProperty("locale", "en");
				
				final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
				JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);
			
			//Update Client Balance
			this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);

			// Update order-price
			this.chargingOrderWritePlatformService.updateBillingOrder(billingOrderCommands);
			 System.out.println("---------------------"+billingOrderCommands.get(0).getNextBillableDate());
			}
		 }
		
		//}
		return new CommandProcessingResult(orderId);
		
	}catch(DataIntegrityViolationException dve){
		handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	
}


private OrderAddons assembleOrderAddons(JsonElement jsonElement,FromJsonHelper fromJsonHelper, Order order,
		          LocalDate startDate,LocalDate endDate, Long contractId) {
	
	OrderAddons orderAddons = OrderAddons.fromJson(jsonElement,fromJsonHelper,order.getId(),startDate,contractId);
	final BigDecimal price=fromJsonHelper.extractBigDecimalWithLocaleNamed("price", jsonElement);
	Plan plan = this.planRepository.findOne(order.getPlanId());
	
	
	 ChargeCodeMaster chargeCodeMaster = chargeCodeRepository.findOne(fromJsonHelper.extractLongNamed("chargeCodeId", jsonElement));
		Contract contract = contractRepository.findOne(orderAddons.getContractId());
			
			if(endDate != null && chargeCodeMaster.getChargeDuration() != contract.getUnits().intValue()  &&
					  chargeCodeMaster.getDurationType().equalsIgnoreCase(contract.getSubscriptionType())){
				
				throw new ChargeCodeAndContractPeriodException(chargeCodeMaster.getBillFrequencyCode(),"addon");
			}
	
	
	OrderPrice orderPrice =new OrderPrice(orderAddons.getServiceId(),chargeCodeMaster.getChargeCode(),chargeCodeMaster.getChargeType(), price,null,
			chargeCodeMaster.getChargeType(),chargeCodeMaster.getChargeDuration().toString(),chargeCodeMaster.getDurationType(),
			startDate.toDate(),endDate,chargeCodeMaster.getTaxInclusive() == 1?true:false,null);
	orderPrice.setCurrencyId(Long.valueOf(plan.getCurrencyId()));
	//OrderDiscount orderDiscount=new OrderDiscount(order, orderPrice,Long.valueOf(0), new Date(), new LocalDate(), "NONE", BigDecimal.ZERO);
	orderPrice.update(order);
	orderPrice.setIsAddon('Y');
	order.addOrderDeatils(orderPrice);
	
	this.orderPriceRepository.saveAndFlush(orderPrice);
	this.orderRepository.saveAndFlush(order);
	List<ServiceMapping> serviceMapping=this.serviceMappingRepository.findOneByServiceId(orderAddons.getServiceId());
	if(!plan.getProvisionSystem().equalsIgnoreCase("None") && serviceMapping.isEmpty()){ throw new AddonEndDateValidationException(orderAddons.getServiceId().toString());}
	String status=StatusTypeEnum.ACTIVE.toString();
	if(!"None".equalsIgnoreCase(serviceMapping.get(0).getProvisionSystem())){
		status=StatusTypeEnum.PENDING.toString();
	}
	if(endDate !=null){
	   orderAddons.setEndDate(endDate.toDate());
	}else{
	  orderAddons.setEndDate(null);
	}
	orderAddons.setProvisionSystem(serviceMapping.get(0).getProvisionSystem());
	orderAddons.setStatus(status);
	orderAddons.setPriceId(orderPrice.getId());
	
	
	return orderAddons; 
}


private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
	// TODO Auto-generated method stub
	
}


@Override
public CommandProcessingResult disconnectOrderAddon(JsonCommand command,Long entityId) {
 try{
	 
	// this.context.authenticatedUser();
	 OrderAddons orderAddons = this.addonsRepository.findOne(entityId);
	 List<ServiceMapping> serviceMapping  =this.serviceMappingRepository.findOneByServiceId(orderAddons.getServiceId());
	 
	 if(!serviceMapping.isEmpty()){
		 if(serviceMapping.get(0).getProvisionSystem().equalsIgnoreCase("None")){
			 orderAddons.setStatus(StatusTypeEnum.DISCONNECTED.toString());
		 }else{
			 Order order=this.orderRepository.findOne(orderAddons.getOrderId());
			 Plan plan = this.planRepository.findOne(order.getPlanId());
			 HardwareAssociation association=this.hardwareAssociationRepository.findOneByOrderId(orderAddons.getOrderId());
			 orderAddons.setStatus(StatusTypeEnum.PENDING.toString());
				this.provisioningWritePlatformService.postOrderDetailsForProvisioning(order, plan.getPlanCode(), UserActionStatusTypeEnum.ADDON_DISCONNECTION.toString(),
						Long.valueOf(0), null,association.getSerialNo(),order.getId(), serviceMapping.get(0).getProvisionSystem(),orderAddons.getId());
		 }
		 
		 this.addonsRepository.save(orderAddons);
		 
	 }else{
		  throw new AddonEndDateValidationException(orderAddons.getServiceId().toString());
	 }
	 return new CommandProcessingResult(entityId);
	 
 }catch(DataIntegrityViolationException dve){
	 handleCodeDataIntegrityIssues(command, dve);
	 return new CommandProcessingResult(Long.valueOf(-1));
 }

}

}
