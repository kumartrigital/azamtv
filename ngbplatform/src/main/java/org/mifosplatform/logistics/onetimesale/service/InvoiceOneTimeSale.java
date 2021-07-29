package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderReadPlatformService;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderWritePlatformService;
import org.mifosplatform.finance.chargeorder.service.GenerateCharges;
import org.mifosplatform.finance.chargeorder.service.GenerateChargesForOrderService;
import org.mifosplatform.finance.chargeorder.service.GenerateDisconnectionCharges;
import org.mifosplatform.finance.chargeorder.service.GenerateReverseChargesOrderService;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.finance.secondarysubscriberdues.service.SecondarySubscriberDuesWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.organisation.internalTransactions.domain.InternalTransactionRepository;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderPriceRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

/**
 * @author Ranjith
 * invoices for device sale and additional fee charges
 */
@Service
public class InvoiceOneTimeSale {

	private final GenerateCharges generateCharge;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final GenerateChargesForOrderService generateChargesForOrderService;
	private final GenerateDisconnectionCharges generateDisconnectionCharge;
	private final GenerateReverseChargesOrderService generateReverseChargesOrderService;
	private final DiscountMasterRepository discountMasterRepository;
	private final ChargingOrderReadPlatformService billingOrderReadPlatformService;
	private final ClientRepositoryWrapper clientRepository;
	private final FromJsonHelper fromJsonHelper;
	private final OrderReadPlatformService orderReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final OrderPriceRepository orderPriceRepository;
	private final OrderRepository orderRepository;
	
	
	
	@Autowired
	public InvoiceOneTimeSale(final GenerateCharges generateCharge,final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final GenerateChargesForOrderService generateChargesForOrderService,final GenerateDisconnectionCharges generateDisconnectionCharge,
			final GenerateReverseChargesOrderService generateReverseChargesOrderService,final DiscountMasterRepository discountMasterRepository,
			final ClientRepositoryWrapper clientRepository,final ChargingOrderReadPlatformService billingOrderReadPlatformService,final FromJsonHelper fromJsonHelper,
			final OrderReadPlatformService orderReadPlatformService,final ConfigurationRepository configurationRepository,final OfficeReadPlatformService officeReadPlatformService,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final OrderPriceRepository orderPriceRepository,final OrderRepository orderRepository) {
		this.generateCharge = generateCharge;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.generateChargesForOrderService = generateChargesForOrderService;
		this.generateDisconnectionCharge = generateDisconnectionCharge;
		this.generateReverseChargesOrderService = generateReverseChargesOrderService;
		this.discountMasterRepository = discountMasterRepository;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.clientRepository = clientRepository;
		this.fromJsonHelper = fromJsonHelper;
		this.orderReadPlatformService = orderReadPlatformService;
		this.configurationRepository = configurationRepository;
		this.officeReadPlatformService = officeReadPlatformService;
		this.orderPriceRepository = orderPriceRepository;
		this.orderRepository = orderRepository;

	}

/**
 * @param clientId
 * @param oneTimeSaleData
 * @param Wallet flag
 */
	public CommandProcessingResult invoiceOneTimeSale(final Long clientId,final OneTimeSaleData oneTimeSaleData, boolean isWalletEnable) {

		Configuration prepaidConfiguration=this.configurationRepository.findOneByName(ConfigurationConstants.PREPAID_SERVICE );
		OfficeData officeData =this.officeReadPlatformService.retriveOfficeDetail(clientId);
		/*JSONObject officeObject = new JSONObject();
		JSONObject clientObject = new JSONObject();
		JSONObject internalTransactionObject = new JSONObject();
		String dateFormat = "dd MMMM yyyy";
		String transactionDate = new SimpleDateFormat(dateFormat).format(DateUtils.getDateOfTenant());
		*/
		List<BillItem> invoiceList = null; 
		
		/*try{*/
		BigDecimal discountRate = BigDecimal.ZERO;
		
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();

		BillingOrderData billingOrderData = new BillingOrderData(oneTimeSaleData.getId(), oneTimeSaleData.getClientId(),DateUtils.getLocalDateOfTenant().toDate(),
				oneTimeSaleData.getChargeCode(),oneTimeSaleData.getChargeType(),oneTimeSaleData.getTotalPrice(),oneTimeSaleData.getTaxInclusive(),oneTimeSaleData.getCurrencyId(),null);

		Client client=this.clientRepository.findOneWithNotFoundDetection(clientId);
		
		DiscountMaster discountMaster=this.discountMasterRepository.findOne(oneTimeSaleData.getDiscountId());
		
		List<DiscountDetails> discountDetails=discountMaster.getDiscountDetails();
		for(DiscountDetails discountDetail:discountDetails){
			if(client.getCategoryType().equals(Long.valueOf(discountDetail.getCategoryType()))){
				discountRate = discountDetail.getDiscountRate();
			}else if(discountRate.equals(BigDecimal.ZERO) && Long.valueOf(discountDetail.getCategoryType()).equals(Long.valueOf(0))){
				discountRate = discountDetail.getDiscountRate();
			}
		}

		DiscountMasterData discountMasterData = new DiscountMasterData(discountMaster.getId(), discountMaster.getDiscountCode(),discountMaster.getDiscountDescription(),
				discountMaster.getDiscountType(),discountRate, null, null);
		
			discountMasterData = this.calculateDiscount(discountMasterData,billingOrderData.getPrice());

		ChargeData billingOrderCommand = this.generateCharge.getOneTimeBill(billingOrderData, discountMasterData);

		billingOrderCommands.add(billingOrderCommand);

		// calculation of invoice
		 invoiceList = this.generateChargesForOrderService.generateCharge(billingOrderCommands);
		
		
		 
		/*if(prepaidConfiguration.isEnabled()){
			if(officeData.getBusinessType().equalsIgnoreCase("Secondary")){
				if(officeData.getSubscriberDues()){
					
					this.secondarySubscriberDuesWritePlatformService.secondarySubscriberDues(clientId,officeData.getId(),invoice.getInvoiceAmount());
				}else{
            		  throw new PlatformDataIntegrityException("No SubscriberDue","No SubscriberDue","No SubscriberDue"); 
            	  }
			}					
		  }*/
		
		
		/*ClientBillInfoData clientBillInfoData =this.clientBillInfoReadPlatformService.retrieveClientBillInfoDetails(clientId);
		
		BigDecimal conversionPrice = this.clientBalanceWritePlatformService.conversion(Long.valueOf(oneTimeSaleData.getCurrencyId()),clientBillInfoData.getBillCurrency(),invoice.getInvoiceAmount());
		*/
		for(BillItem invoice:invoiceList)
		{
		JSONObject clientBalanceObject = new JSONObject();
		try {
			clientBalanceObject.put("id", invoice.getId());
			clientBalanceObject.put("clientId",clientId);
			clientBalanceObject.put("amount",invoice.getInvoiceAmount());
			clientBalanceObject.put("isWalletEnable", isWalletEnable);
			if(oneTimeSaleData.getClientServiceId()==null)
			{
			clientBalanceObject.put("clientServiceId",0);
			}else {
			clientBalanceObject.put("clientServiceId",oneTimeSaleData.getClientServiceId());
			}
			clientBalanceObject.put("currencyId",invoice.getCurrencyId());
			clientBalanceObject.put("locale", "en");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
		JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);
	
		// Update Client Balance
		this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
		}
	/*} catch (JSONException e) {
		e.printStackTrace();
	}*/
		return new CommandProcessingResult(invoiceList.get(0).getId());

	}

/**
 * @param clientId
 * @param oneTimeSaleData
 * @param invoice 
 * @param wallet 
 *  reverse invoice 
 */
	public CommandProcessingResult reverseInvoiceForOneTimeSale(final Long clientId, final OneTimeSaleData oneTimeSaleData,final BigDecimal discountAmount,final boolean isWalletEnable) {
        
		
		BigDecimal discountRate = BigDecimal.ZERO;
		
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();

		BillingOrderData billingOrderData = new BillingOrderData(oneTimeSaleData.getId(), clientId, DateUtils.getLocalDateOfTenant().toDate(),
				oneTimeSaleData.getChargeCode(),oneTimeSaleData.getChargeType(),oneTimeSaleData.getTotalPrice(),oneTimeSaleData.getTaxInclusive(),oneTimeSaleData.getCurrencyId(),null);
		

		DiscountMaster discountMaster=this.discountMasterRepository.findOne(oneTimeSaleData.getDiscountId());
		
		DiscountMasterData discountMasterData = new DiscountMasterData(discountMaster.getId(), discountMaster.getDiscountCode(),discountMaster.getDiscountDescription(),
				discountMaster.getDiscountType(),discountRate, null, null,discountAmount);

		ChargeData billingOrderCommand = this.generateDisconnectionCharge.getReverseOneTimeBill(billingOrderData, discountMasterData);
		
		 billingOrderCommands.add(billingOrderCommand);

		// calculation of reverse invoice
		List<BillItem> invoiceList = this.generateReverseChargesOrderService.generateNegativeCharge(billingOrderCommands);
		for(BillItem invoice : invoiceList)
		{
		
		JSONObject clientBalanceObject = new JSONObject();
		try {
			clientBalanceObject.put("id", invoice.getId());
			clientBalanceObject.put("clientId",invoice.getClientId());
			clientBalanceObject.put("amount", invoice.getInvoiceAmount());
			clientBalanceObject.put("isWalletEnable", isWalletEnable);
			if(oneTimeSaleData.getClientServiceId()==null)
			{
			clientBalanceObject.put("clientServiceId",0);
			}else {
			clientBalanceObject.put("clientServiceId",oneTimeSaleData.getClientServiceId());
			}
			clientBalanceObject.put("currencyId",invoice.getCurrencyId());
			clientBalanceObject.put("locale", "en");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
		JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);
		

		// To fetch record from client_balance table
		this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
		}
		return new CommandProcessingResult(invoiceList.get(0).getId());

	}
	

	/**
	 * @param chargeMaster
	 * @param orderId
	 * @param priceId
	 * @param clientId
	 * @param feeChargeAmount
	 * @return invoice
	 */
	public BillItem calculateAdditionalFeeCharges(final ChargeCodeMaster chargeMaster,final Long orderId, final Long priceId, 
			                      final Long clientId, final BigDecimal ChargeAmount) {
		Order order = this.orderRepository.findOne(orderId);
		OrderPrice orderPrice = this.orderPriceRepository.findOrders(order);
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();
		List<ChargeTaxCommand>  listOfTaxes = this.calculateTax(clientId, ChargeAmount,chargeMaster);
		ChargeData billingOrderCommand = new ChargeData(orderId,priceId,clientId, DateUtils.getDateOfTenant(),
				DateUtils.getDateOfTenant(),DateUtils.getDateOfTenant(),chargeMaster.getBillFrequencyCode(), chargeMaster.getChargeCode(),
				chargeMaster.getChargeType(),chargeMaster.getChargeDuration(), "",DateUtils.getDateOfTenant(),ChargeAmount, 
				"N",listOfTaxes, DateUtils.getDateOfTenant(),DateUtils.getDateOfTenant(), null,chargeMaster.getTaxInclusive(),false,orderPrice.getCurrencyId(),null);

		billingOrderCommands.add(billingOrderCommand);
		
		List<BillItem> invoiceList = this.generateChargesForOrderService.generateCharge(billingOrderCommands);
		List<OrderData> orderData = this.orderReadPlatformService.orderDetailsForClientBalance(orderId);
		
		for(BillItem invoice:invoiceList)
		{
		JSONObject clientBalanceObject = new JSONObject();
		try {
			clientBalanceObject.put("id", invoice.getId());
			clientBalanceObject.put("clientId",clientId);
			clientBalanceObject.put("amount", invoice.getInvoiceAmount());
			clientBalanceObject.put("isWalletEnable", false);
			clientBalanceObject.put("locale", "en");
			clientBalanceObject.put("clientServiceId",orderData.get(0).getClientServiceId());
			clientBalanceObject.put("currencyId",invoice.getCurrencyId());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
		JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);
	
		
		this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
		}
		return invoiceList.get(0);
	}
	
	/**
	 * @param clientId
	 * @param chargeAmount
	 * @param chargeMaster
	 * @return
	 */
	public List<ChargeTaxCommand> calculateTax(Long clientId,BigDecimal billPrice, ChargeCodeMaster chargeMaster) {

		// Get State level taxes
		List<TaxMappingRateData> taxMappingRateDatas = this.billingOrderReadPlatformService.retrieveTaxMappingData(clientId,chargeMaster.getChargeCode());
		if (taxMappingRateDatas.isEmpty()) {
			taxMappingRateDatas = this.billingOrderReadPlatformService.retrieveDefaultTaxMappingData(clientId,chargeMaster.getChargeCode());
		}
		List<ChargeTaxCommand> invoiceTaxCommand = this.generateCharge.generateInvoiceTax(taxMappingRateDatas, billPrice, clientId,chargeMaster.getTaxInclusive());
		
		return invoiceTaxCommand;

	}

	// Discount Applicable Logic
	public boolean isDiscountApplicable(final DiscountMasterData discountMasterData) {
		boolean isDiscountApplicable = true;
		
		return isDiscountApplicable;

	}

	// Discount End Date calculation if null
	public Date getDiscountEndDateIfNull(final DiscountMasterData discountMasterData) {
		LocalDate discountEndDate = discountMasterData.getDiscountEndDate();
		if (discountMasterData.getDiscountEndDate() == null) {
			discountEndDate = new LocalDate(2099, 0, 01);
		}
		return discountEndDate.toDate();

	}
	
	// if is percentage
	public boolean isDiscountPercentage(final DiscountMasterData discountMasterData){
		boolean isDiscountPercentage = false;
		if(discountMasterData.getDiscountType().equalsIgnoreCase("percentage")){
																
			isDiscountPercentage = true;
		}
		return isDiscountPercentage;
	}
	
	// if is discount
	public boolean isDiscountFlat(final DiscountMasterData discountMasterData){
		boolean isDiscountFlat = false;
		if(discountMasterData.getDiscountType().equalsIgnoreCase("flat")){
			
			isDiscountFlat = true;
		}
		return isDiscountFlat;
	}
	

	// Discount calculation 
	public DiscountMasterData calculateDiscount(final DiscountMasterData discountMasterData, BigDecimal chargePrice){
		
		BigDecimal discountAmount=BigDecimal.ZERO;
		if(isDiscountPercentage(discountMasterData)){
			
			if(discountMasterData.getDiscountRate().compareTo(new BigDecimal(100)) ==-1 ||
			 discountMasterData.getDiscountRate().compareTo(new BigDecimal(100)) == 0){
				
			discountAmount = this.calculateDiscountPercentage(discountMasterData.getDiscountRate(), chargePrice);
			discountMasterData.setDiscountAmount(discountAmount);
			chargePrice = this.chargePriceNotLessThanZero(chargePrice, discountAmount);
			discountMasterData.setDiscountedChargeAmount(chargePrice);
			
			}
			
		}
		
		if(isDiscountFlat(discountMasterData)){
			
			BigDecimal netFlatAmount=this.calculateDiscountFlat(discountMasterData.getDiscountRate(), chargePrice);
			netFlatAmount=this.chargePriceNotLessThanZero(chargePrice, discountMasterData.getDiscountRate());
			discountMasterData.setDiscountedChargeAmount(netFlatAmount);
			discountAmount = chargePrice.subtract(netFlatAmount);
			discountMasterData.setDiscountAmount(discountAmount);
			
		}
		return discountMasterData;
	
	}
	
	// Discount Percent calculation
	public BigDecimal calculateDiscountPercentage(final BigDecimal discountRate,final BigDecimal chargePrice){
		
		return chargePrice.multiply(discountRate.divide(new BigDecimal(100))).setScale(Integer.parseInt(this.generateCharge.roundingDecimal()), RoundingMode.HALF_UP);
	}
	
	// Discount Flat calculation
	public BigDecimal calculateDiscountFlat(final BigDecimal discountRate,final BigDecimal chargePrice){
		
		BigDecimal discountFlat=BigDecimal.ZERO;
		//check for charge price zero and discount rate greater than zero
		if(chargePrice.compareTo(BigDecimal.ZERO) == 1 ){
			discountFlat=chargePrice.subtract(discountRate).setScale(Integer.parseInt(this.generateCharge.roundingDecimal()),RoundingMode.HALF_UP);
		}
		return discountFlat;
	}
	
	// to check price not less than zero
	public BigDecimal chargePriceNotLessThanZero(BigDecimal chargePrice,final BigDecimal discountPrice){
		
		chargePrice = chargePrice.subtract(discountPrice);
		if(chargePrice.compareTo(discountPrice) < 0){
			chargePrice = BigDecimal.ZERO;
		}
		return chargePrice;
		
	}

}
