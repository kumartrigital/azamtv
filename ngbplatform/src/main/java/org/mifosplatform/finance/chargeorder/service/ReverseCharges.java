package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ReverseCharges {
	
	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateReverseChargesOrderService generateReverseChargesOrderService;
	private final GenerateChargesForOrderService generateChargesForOrderService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final BillItemRepository  billItemRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	
	
	@Autowired
	public ReverseCharges(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,final GenerateChargesForOrderService generateChargesForOrderService,
			final GenerateReverseChargesOrderService generateReverseChargesOrderService,final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final BillItemRepository billItemRepository,final OrderReadPlatformService orderReadPlatformService,final FromJsonHelper fromJsonHelper){
		
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateReverseChargesOrderService = generateReverseChargesOrderService;
		this.chargingOrderWritePlatformService=chargingOrderWritePlatformService;
		this.generateChargesForOrderService=generateChargesForOrderService;
		this.billItemRepository = billItemRepository;
		this.orderReadPlatformService = orderReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}
	
	 
	public BigDecimal reverseInvoiceServices(final Long orderId,final Long clientId,final LocalDate disconnectionDate){
		
		List<BillItem> invoiceList=null;
		
		BillItem invoice=null;
	    BigDecimal invoiceAmount=BigDecimal.ZERO;
	   
		List<BillingOrderData> billingOrderProducts = this.chargingOrderReadPlatformService.getReverseBillingOrderData(clientId, disconnectionDate, orderId);
		
		List<ChargeData> billingOrderCommands = this.generateReverseChargesOrderService.generateReverseBillingOrder(billingOrderProducts,disconnectionDate);
		
		if(billingOrderCommands.size() !=0){
			
		if(billingOrderCommands.get(0).getChargeType().equalsIgnoreCase("RC")){
			 invoiceList = this.generateChargesForOrderService. generateCharge(billingOrderCommands);
			 for(BillItem invioce : invoiceList)
		        {
		        	invoiceAmount= invoiceAmount.add(invoice.getInvoiceAmount());
		        }
			 invoiceAmount=invoiceList.get(0).getInvoiceAmount();
		}else{
			
		List<BillItem> invoiceItems = this.generateReverseChargesOrderService.generateNegativeCharge(billingOrderCommands);
        
        for(BillItem invioce : invoiceItems)
        {
        	invoiceAmount= invoiceAmount.add(invoice.getInvoiceAmount());
        }
        List<Long>  invoices = this.chargingOrderReadPlatformService.listOfInvoices(clientId, orderId);
	        if(!invoices.isEmpty() && invoiceAmount != null && invoiceAmount.intValue() != 0){
	        
	        	for(Long invoiceIds :invoices){
		        	Long invoiceId = invoiceIds;
		        	BillItem invoiceData = this.billItemRepository.findOne(invoiceId);
		        	BigDecimal dueAmount = invoiceData.getDueAmount();
		        	if(dueAmount != null && dueAmount.intValue() > 0 && invoiceAmount.intValue() < dueAmount.intValue()){
		        		BigDecimal updateAmount = dueAmount.add(invoiceAmount);
		        		invoiceData.setDueAmount(updateAmount);
		        		this.billItemRepository.saveAndFlush(invoiceData);
		        	}else if(dueAmount != null && dueAmount.intValue() > 0 && invoiceAmount.intValue() > dueAmount.intValue()){
		        		invoiceData.setDueAmount(BigDecimal.ZERO);
		        		this.billItemRepository.saveAndFlush(invoiceData);
		        	}
		        }
	        }
	        
		}
		
		
		List<OrderData> orderData = this.orderReadPlatformService.orderDetailsForClientBalance(orderId);
		
		JsonObject clientBalanceObject = new JsonObject();
			clientBalanceObject.addProperty("clientId",clientId);
			clientBalanceObject.addProperty("amount", invoice.getInvoiceAmount());
			clientBalanceObject.addProperty("isWalletEnable", false);
			clientBalanceObject.addProperty("clientServiceId",orderData.get(0).getClientServiceId());
			clientBalanceObject.addProperty("currencyId",invoice.getCurrencyId());
			clientBalanceObject.addProperty("locale", "en");
	
	
	final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
	JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);

		
		this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
		
		this.chargingOrderWritePlatformService.updateBillingOrder(billingOrderCommands);
		 
		return invoiceAmount;
	}else{
		return invoiceAmount;
	}
		}
	
	
}

	
