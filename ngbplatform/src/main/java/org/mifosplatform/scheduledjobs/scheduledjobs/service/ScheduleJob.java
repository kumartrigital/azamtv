package org.mifosplatform.scheduledjobs.scheduledjobs.service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderReadPlatformService;
import org.mifosplatform.finance.chargeorder.service.GenerateCharges;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBillingRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.product.domain.ProductRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class ScheduleJob {

private final ClientBalanceRepository clientBalanceRepository;	
private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
private final GenerateCharges generateCharges;
private final OrderRepository orderRepository;
private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
private final FromJsonHelper fromApiJsonHelper;
private final OrderWritePlatformService orderWritePlatformService;
private final PlanRepository planRepository;
private final PriceRepository priceRepository;
private final ServiceMasterRepository serviceMasterRepository;
private final ContractRepository contractRepository;
private final PaypalRecurringBillingRepository paypalRecurringBillingRepository;
private final ProductRepository productRepository;

@Autowired
public ScheduleJob(final ClientBalanceRepository clientBalanceRepository,final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
final GenerateCharges generateCharges,final OrderRepository orderRepository,final ContractPeriodReadPlatformService contractPeriodReadPlatformService,
final FromJsonHelper fromApiJsonHelper,final OrderWritePlatformService orderWritePlatformService,final PriceRepository priceRepository,
final PaypalRecurringBillingRepository paypalRecurringBillingRepository,final PlanRepository planRepository,
final ServiceMasterRepository serviceMasterRepository,final ContractRepository contractRepository,final ProductRepository productRepository){

	this.clientBalanceRepository=clientBalanceRepository;
	this.chargingOrderReadPlatformService=chargingOrderReadPlatformService;
	this.generateCharges=generateCharges;
	this.orderRepository=orderRepository;
	this.priceRepository = priceRepository;
	this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
	this.fromApiJsonHelper=fromApiJsonHelper;
	this.orderWritePlatformService=orderWritePlatformService;
	this.serviceMasterRepository = serviceMasterRepository;
	this.paypalRecurringBillingRepository = paypalRecurringBillingRepository;
	this.planRepository = planRepository;
	this.contractRepository = contractRepository;
	this.productRepository = productRepository;

}

        
public boolean checkClientBalanceForOrderrenewal(OrderData orderData,Long clientId, List<OrderPrice> orderPrices,Long clientServiceId) {
	

	 boolean isAmountSufficient=false;
     //ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(clientId);
	 ClientBalance clientBalance=this.clientBalanceRepository.findByClientAndClientServiceId(clientId,clientServiceId);
     if(clientBalance!=null){
         BigDecimal resultanceBal=clientBalance.getBalanceAmount();
      if(resultanceBal.compareTo(BigDecimal.ZERO) != 1){
      isAmountSufficient=true;
      }
     }
      return isAmountSufficient;
      
	/*

           BigDecimal discountAmount=BigDecimal.ZERO;

           List<DiscountMasterData> discountMasterDatas = chargingOrderReadPlatformService.retrieveDiscountOrders(orderData.getId(),orderPrices.get(0).getId());
         DiscountMasterData discountMasterData=null;
         if(!discountMasterDatas.isEmpty()){
         discountMasterData=discountMasterDatas.get(0);
         }
        
         boolean isAmountSufficient=false;
         ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(clientId);
         BigDecimal orderPrice=new BigDecimal(orderData.getPrice());
          
       if(this.generateCharges.isDiscountApplicable(DateUtils.getLocalDateOfTenant(),discountMasterData,DateUtils.getLocalDateOfTenant().plusMonths(1))){
          discountMasterData = this.generateCharges.calculateDiscount(discountMasterData,  orderPrice);
          discountAmount=discountMasterData.getDiscountAmount();
         }
        orderPrice=orderPrice.subtract(discountAmount);
        BigDecimal reqRenewalAmount=orderPrice;//orderPrice.divide(new BigDecimal(2),RoundingMode.CEILING);
    
         if(clientBalance!=null){
            BigDecimal resultanceBal=clientBalance.getBalanceAmount().add(reqRenewalAmount);
         if(resultanceBal.compareTo(BigDecimal.ZERO) != 1){
         isAmountSufficient=true;
         }
          }

return isAmountSufficient;
<<<<<<< HEAD
*/}


public void ProcessAutoExipiryDetails(OrderData orderData, FileWriter fw, LocalDate exipirydate, JobParameterData data, Long clientId, boolean isSufficientAmountForRenewal) {
	  
	 try{

	      if(!(orderData.getStatus().equalsIgnoreCase(StatusTypeEnum.DISCONNECTED.toString()) || orderData.getStatus().equalsIgnoreCase(StatusTypeEnum.PENDING.toString()))){

	           if (orderData.getEndDate().equals(exipirydate) || exipirydate.isAfter(orderData.getEndDate())){
	            
	                 JSONObject jsonobject = new JSONObject();
	                     if(data.getIsAutoRenewal().equalsIgnoreCase("Y") || orderData.getAutoRenew().equalsIgnoreCase("Y")){
	                      
	                            Order order=this.orderRepository.findOne(orderData.getId());
	                           // List<OrderPrice> orderPrice=order.getPrice();
	                            if(order.isAutoRenewal() == 'Y' || isSufficientAmountForRenewal && order.isAutoRenewal() == 'N'){
	                            	//boolean isSufficientAmountForRenewal=this.checkClientBalanceForOrderrenewal(orderData,clientId,orderPrice);   	

	                          if(isSufficientAmountForRenewal){
	                           Plan plan=this.planRepository.findOne(order.getPlanId());
	                           
	                             //List<SubscriptionData> subscriptionDatas=this.contractPeriodReadPlatformService.retrieveSubscriptionDatabyContractType("Month(s)",1);
	                        	  if(plan.isPrepaid() == 'Y'){
	                        		 // ServiceMaster serviceMaster = this.serviceMasterRepository.findOne(order.getPrice().get(0).getServiceId());
	                        		  Product serviceMaster = this.productRepository.findOne(order.getPrice().get(0).getProductId());
	                        		  Contract contract =this.contractRepository.findOne(order.getContarctPeriod());
	                        		  List<Price> prices=this.priceRepository.findOneByPlanAndService(order.getPlanId(),serviceMaster.getServiceId(),contract.getSubscriptionPeriod(),order.getPrice().get(0).getChargeCode());
	                        		  if(!prices.isEmpty()){
	                        		  jsonobject.put("priceId",prices.get(0).getId());
	                        		  }
	                        	  }

	                             jsonobject.put("renewalPeriod",order.getContarctPeriod()); 
	                             jsonobject.put("description","Order Renewal By Scheduler");
	                             
	                             
	                             final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                             final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"RENEWAL",order.getClientId(), null,
	                               null,order.getClientId(), null, null, null,null, null, null,null);
	                             fw.append("sending json data for Renewal Order is : "+jsonobject.toString()+"\r\n");
	                             this.orderWritePlatformService.renewalClientOrder(command,orderData.getId());
	                             fw.append("Client Id"+clientId+" With this Orde"+orderData.getId()+" has been renewaled for one month via " +"Auto Exipiry on Dated"+exipirydate);

	                          }else{
	                              SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	                              jsonobject.put("disconnectReason","Date Expired");
	                              jsonobject.put("disconnectionDate",dateFormat.format(orderData.getEndDate().toDate()));
	                              jsonobject.put("dateFormat","dd MMMM yyyy");
	                              jsonobject.put("locale","en");
	                              fw.append("sending json data for Disconnecting the Order is : "+jsonobject.toString()+"\r\n");
	                              
	                              final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                              final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",order.getClientId(), null,
	                                     null,order.getClientId(), null, null, null,null, null, null,null);
	                              this.orderWritePlatformService.disconnectOrder(command, orderData.getId());
	                              fw.append("Client Id"+order.getClientId()+" With this Orde"+order.getId()+" has been disconnected via Auto Exipiry on Dated"+exipirydate);
	                          }
	                     }else{
	                    	 
	                    	 SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	                            jsonobject.put("disconnectReason","Date Expired");
	                            jsonobject.put("disconnectionDate",dateFormat.format(orderData.getEndDate().toDate()));
	                            jsonobject.put("dateFormat","dd MMMM yyyy");
	                            jsonobject.put("locale","en");
	                            final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                            final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",clientId, null,
	                                   null,clientId, null, null, null,null, null, null,null);
	                            this.orderWritePlatformService.disconnectOrder(command, orderData.getId());
	                            fw.append("Client Id"+clientId+" With this Orde"+orderData.getId()+" has been disconnected via Auto Exipiry on Dated"+exipirydate);
	                    	 
	                     }
	                     }else{// if (orderData.getEndDate().equals(exipirydate) || exipirydate.toDate().after((orderData.getEndDate().toDate()))){



	                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	                            jsonobject.put("disconnectReason","Date Expired");
	                            jsonobject.put("disconnectionDate",dateFormat.format(orderData.getEndDate().toDate()));
	                            jsonobject.put("dateFormat","dd MMMM yyyy");
	                            jsonobject.put("locale","en");
	                            final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                            final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",clientId, null,
	                                   null,clientId, null, null, null,null, null, null,null);
	                            this.orderWritePlatformService.disconnectOrder(command, orderData.getId());
	                            fw.append("Client Id"+clientId+" With this Orde"+orderData.getId()+" has been disconnected via Auto Exipiry on Dated"+exipirydate);
	                   }
	           }
	      }
	 } catch(IOException exception){    
		 exception.printStackTrace();     
	 } catch(Exception exception){
		 exception.printStackTrace();  
	 }	
}



/**
 * @param order
 * @param fw
 * @param clientId
 */
public void ProcessDisconnectUnPaidCustomers(OrderData order,FileWriter fw, Long clientId) {
	
	try{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		JSONObject jsonobject = new JSONObject();
        jsonobject.put("disconnectReason","Payment Due");
        jsonobject.put("disconnectionDate",dateFormat.format(DateUtils.getDateOfTenant()));
        jsonobject.put("dateFormat","dd MMMM yyyy");
        jsonobject.put("locale","en");
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
        final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",clientId, null,
               null,clientId, null, null, null,null, null, null,null);
        this.orderWritePlatformService.disconnectOrder(command, order.getId());
        fw.append("Client Id "+clientId+" With this Order "+order.getId()+" has been disconnected via Payment Due on Dated "+dateFormat.format(DateUtils.getDateOfTenant())+ "\r\n");
		
	}catch(Exception exception){    
			 exception.printStackTrace();     
		 } 
   }
}