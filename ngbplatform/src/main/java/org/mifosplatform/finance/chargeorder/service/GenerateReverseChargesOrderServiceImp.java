package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.chargeorder.domain.ChargeTax;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author hugo
 *s
 */
@Service
public class GenerateReverseChargesOrderServiceImp implements GenerateReverseChargesOrderService {

	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateDisconnectionCharges generateDisconnectionCharges;
	private final BillItemRepository billItemRepository;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final ClientBalanceWritePlatformService clientBalanceWritePlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	

	@Autowired
	public GenerateReverseChargesOrderServiceImp(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final GenerateDisconnectionCharges generateDisconnectionCharges,final BillItemRepository billItemRepository,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService,final ClientBalanceWritePlatformService clientBalanceWritePlatformService, final OfficeReadPlatformService officeReadPlatformService) {

		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateDisconnectionCharges = generateDisconnectionCharges;
		this.billItemRepository = billItemRepository;
		this.clientBillInfoReadPlatformService=clientBillInfoReadPlatformService;
		this.clientBalanceWritePlatformService=clientBalanceWritePlatformService;
		this.officeReadPlatformService = officeReadPlatformService;
	}

	@Override
	public List<ChargeData> generateReverseBillingOrder(final List<BillingOrderData> billingOrderProducts,final LocalDate disconnectDate) {

		ChargeData billingOrderCommand = null;
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();
		
		if (billingOrderProducts.size() != 0) {

			for (BillingOrderData billingOrderData : billingOrderProducts) {

				DiscountMasterData discountMasterData = null;

		      List<DiscountMasterData> discountMasterDatas = chargingOrderReadPlatformService.retrieveDiscountOrders(billingOrderData.getClientOrderId(),
		    		                                               billingOrderData.getOderPriceId());
				if (discountMasterDatas.size() != 0) {
					discountMasterData = discountMasterDatas.get(0);
				}

				if (generateDisconnectionCharges.isChargeTypeRC(billingOrderData)) {

					// monthly
					if (billingOrderData.getDurationType().equalsIgnoreCase("month(s)")) {

						billingOrderCommand = generateDisconnectionCharges.getReverseMonthyBill(billingOrderData,discountMasterData, disconnectDate);
						billingOrderCommands.add(billingOrderCommand);
					}
					// weekly	
					else if (billingOrderData.getDurationType().equalsIgnoreCase("week(s)")) {

						billingOrderCommand = generateDisconnectionCharges.getReverseWeeklyBill(billingOrderData,discountMasterData,disconnectDate);
						billingOrderCommands.add(billingOrderCommand);
				 }
				}
			}

		}

		return billingOrderCommands;
	}

	@Override
	public List<BillItem> generateNegativeCharge(final List<ChargeData> billingOrderCommands) {
		
		BigDecimal invoiceAmount = BigDecimal.ZERO;
		BigDecimal totalChargeAmount = BigDecimal.ZERO;
		BigDecimal netTaxAmount = BigDecimal.ZERO;
		ClientBillInfoData clientBillInfoData = null;
		List<BillItem> invoiceList = new ArrayList<>();
		Map<Long,BillItem> billItemMap = new HashMap<Long,BillItem>();
		Charge charge = null;

		
		/*
		 * BillItem invoice = new BillItem(billingOrderCommands.get(0).getClientId(),
		 * DateUtils.getLocalDateOfTenant().toDate(), invoiceAmount,
		 * invoiceAmount,netTaxAmount, "active");
		 */
		
		for (ChargeData billingOrderCommand : billingOrderCommands) {
			BillItem invoice = null;
			OfficeData officeData = null;
			if(billingOrderCommand.getChargeOwner().equalsIgnoreCase("self"))
			{
				if(billItemMap.containsKey(billingOrderCommand.getClientId()))
				{
			 invoice = billItemMap.get(billingOrderCommand.getClientId());
					}else {
					 invoice = new BillItem(billingOrderCommand.getClientId(),
							DateUtils.getLocalDateOfTenant().toDate(), invoiceAmount, invoiceAmount, netTaxAmount, "active");
					 billItemMap.put(billingOrderCommand.getClientId(), invoice);
				}
			}else 
			{
				 officeData = this.officeReadPlatformService.retriveOfficeDetail(billingOrderCommand.getClientId());

				if(billItemMap.containsKey(officeData.getClientId())) {
				 invoice = billItemMap.get(officeData.getClientId());
				}else 
				{
					 invoice = new BillItem(officeData.getClientId(),
							DateUtils.getLocalDateOfTenant().toDate(), invoiceAmount, invoiceAmount, netTaxAmount, "active");
					 billItemMap.put(officeData.getClientId(), invoice);
				}
			}
			clientBillInfoData =this.clientBillInfoReadPlatformService.retrieveClientBillInfoDetails(billingOrderCommand.getClientId());
			if (billingOrderCommand.getCurrencyId() <= 1000) {
		    BigDecimal conversionPrice = this.clientBalanceWritePlatformService.conversion(billingOrderCommand.getCurrencyId(),clientBillInfoData.getBillCurrency(),billingOrderCommand.getPrice());

			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			String discountCode="None";
			BigDecimal discountAmount = BigDecimal.ZERO;
		    //BigDecimal netChargeAmount = billingOrderCommand.getPrice();
		    BigDecimal netChargeAmount = conversionPrice;
		    
			if(billingOrderCommand.getDiscountMasterData()!= null){
				 discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				 discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode();
				 if(billingOrderCommand.getChargeType().equalsIgnoreCase("NRC")){
				  netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);
				 }
			}
			
			List<ChargeTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();
			
			clientBillInfoData =this.clientBillInfoReadPlatformService.retrieveClientBillInfoDetails(billingOrderCommand.getClientId());
			
			if(billingOrderCommand.getChargeOwner().equalsIgnoreCase("self"))
			{
			 charge = new Charge(billingOrderCommand.getClientId(), billingOrderCommand.getClientOrderId(), billingOrderCommand.getOrderPriceId(),

					billingOrderCommand.getChargeCode(),billingOrderCommand.getChargeType(),discountCode, conversionPrice.negate(), discountAmount.negate(),
					netChargeAmount.negate(), billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),clientBillInfoData.getBillCurrency(),billingOrderCommand.getChargeOwner());
			}else 
			{
				charge = new Charge(clientBillInfoData.getOfficeClientId(), billingOrderCommand.getClientOrderId(), billingOrderCommand.getOrderPriceId(),

						billingOrderCommand.getChargeCode(),billingOrderCommand.getChargeType(),discountCode, conversionPrice.negate(), discountAmount.negate(),
						netChargeAmount.negate(), billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),clientBillInfoData.getBillCurrency(),billingOrderCommand.getChargeOwner());
			}
			if(!invoiceTaxCommands.isEmpty()){
			
			     for(ChargeTaxCommand invoiceTaxCommand : invoiceTaxCommands){
				
			    	if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) != 0) {
			    		
				     netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
				     ChargeTax invoiceTax = new ChargeTax(invoice, charge, invoiceTaxCommand.getTaxCode(),invoiceTaxCommand.getTaxValue(), 
						                  invoiceTaxCommand.getTaxPercentage(), invoiceTaxCommand.getTaxAmount().negate(),clientBillInfoData.getBillCurrency());
				      charge.addChargeTaxes(invoiceTax);
			    	}
			     }


			     if (billingOrderCommand.getTaxInclusive() != null) {
						
						if (isTaxInclusive(billingOrderCommand.getTaxInclusive())&&invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
							netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
							charge.setNetChargeAmount(netChargeAmount.negate());
							charge.setChargeAmount(netChargeAmount.negate());
						}
					}

			}
			netTaxAmount = netTaxAmount.add(netChargeTaxAmount);
			totalChargeAmount = totalChargeAmount.add(netChargeAmount);
			invoice.addCharges(charge);		
			invoiceAmount = totalChargeAmount.add(netTaxAmount);
			invoice.setNetChargeAmount(totalChargeAmount.negate());
			invoice.setTaxAmount(netTaxAmount.negate());
			invoice.setInvoiceAmount(invoiceAmount.negate());
			invoice.setCurrencyId(clientBillInfoData.getBillCurrency());
			if(billingOrderCommand.getChargeOwner().equalsIgnoreCase("self"))
			{
				billItemMap.put(billingOrderCommand.getClientId(), invoice);
			}else
			{
				billItemMap.put(officeData.getClientId(), invoice);
			}
		 }
			for (Map.Entry<Long, BillItem> entry : billItemMap.entrySet()) {
				this.billItemRepository.saveAndFlush(entry.getValue());
				invoiceList.add(entry.getValue());
		    }
		}

		
		//return this.billItemRepository.save(invoice);
		return invoiceList;
	}
	
	public Boolean isTaxInclusive(Integer taxInclusive){
		
		Boolean isTaxInclusive = false;
		if(taxInclusive == 1){ isTaxInclusive = true;}
		return isTaxInclusive;
	}
}
