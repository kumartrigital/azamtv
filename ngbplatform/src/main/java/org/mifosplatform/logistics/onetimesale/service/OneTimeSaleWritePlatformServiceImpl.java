package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.jackrabbit.value.LongValue;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.discountmaster.service.DiscountReadPlatformService;
import org.mifosplatform.cms.eventorder.exception.InsufficientAmountException;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.grn.service.GrnReadPlatformService;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.domain.UnitEnumType;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrn;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairing;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairingRepository;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSale;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSaleRepository;
import org.mifosplatform.logistics.onetimesale.exception.DeviceSaleNotFoundException;
import org.mifosplatform.logistics.onetimesale.serialization.OneTimesaleCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.slabRate.service.SlabRateWritePlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.workflow.eventvalidation.service.EventValidationReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author hugo
 *
 */
@Service
public class OneTimeSaleWritePlatformServiceImpl implements OneTimeSaleWritePlatformService {
	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(OneTimeSaleWritePlatformServiceImpl.class);
	private final FromJsonHelper fromJsonHelper;
	private final PlatformSecurityContext context;
	private final ItemRepository itemMasterRepository;
	private final OneTimesaleCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final InvoiceOneTimeSale invoiceOneTimeSale;
	private final OneTimeSaleRepository oneTimeSaleRepository;
	private final ItemReadPlatformService itemReadPlatformService;
	private final DiscountReadPlatformService discountReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final EventValidationReadPlatformService eventValidationReadPlatformService;
	private final ChargeCodeRepository chargeCodeRepository;
	private final BillItemRepository billItemRepository;
	private final InventoryGrnRepository inventoryGrnRepository;
	private final GrnReadPlatformService grnReadPlatformService;
	private final ItemDetailsRepository itemDetailsRepository;
	private final ItemPairingRepository itemPairingRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final OrderRepository orderRepository;
	private final ClientBalanceReadPlatformService  clientBalanceReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final SlabRateWritePlatformService slabRateWritePlatformService;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final ClientBalanceWritePlatformService clientBalanceWritePlatformService;

	@Autowired
	public OneTimeSaleWritePlatformServiceImpl(final PlatformSecurityContext context,final OneTimeSaleRepository oneTimeSaleRepository,
			final ItemRepository itemMasterRepository,final OneTimesaleCommandFromApiJsonDeserializer apiJsonDeserializer,
			final InvoiceOneTimeSale invoiceOneTimeSale,final ItemReadPlatformService itemReadPlatformService,
			final FromJsonHelper fromJsonHelper,final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,
			final EventValidationReadPlatformService eventValidationReadPlatformService,
			final DiscountReadPlatformService discountReadPlatformService,
			final ChargeCodeRepository chargeCodeRepository,
			final BillItemRepository billItemRepository, final InventoryGrnRepository inventoryGrnRepository,
			final GrnReadPlatformService grnReadPlatformService,final ItemDetailsRepository itemDetailsRepository,
			final ItemPairingRepository itemPairingRepository,final FromJsonHelper fromApiJsonHelper,
			final OrderRepository orderRepository,
			final ClientBalanceReadPlatformService clientBalanceReadPlatformService,
			final ConfigurationRepository configurationRepository,
			final OfficeReadPlatformService officeReadPlatformService,
			final ClientReadPlatformService clientReadPlatformService,final SlabRateWritePlatformService slabRateWritePlatformService,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService,
			final ClientBalanceWritePlatformService clientBalanceWritePlatformService) {

		this.context = context;
		this.fromJsonHelper = fromJsonHelper;
		this.invoiceOneTimeSale = invoiceOneTimeSale;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.itemMasterRepository = itemMasterRepository;
		this.oneTimeSaleRepository = oneTimeSaleRepository;
		this.itemReadPlatformService = itemReadPlatformService;
		this.discountReadPlatformService = discountReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
		this.eventValidationReadPlatformService = eventValidationReadPlatformService;
		this.chargeCodeRepository = chargeCodeRepository;
		this.billItemRepository = billItemRepository;
		this.inventoryGrnRepository = inventoryGrnRepository;
		this.grnReadPlatformService = grnReadPlatformService;
		this.itemDetailsRepository = itemDetailsRepository;
		this.itemPairingRepository = itemPairingRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.orderRepository = orderRepository;
		this.clientBalanceReadPlatformService=clientBalanceReadPlatformService;
		this.configurationRepository=configurationRepository;
		this.officeReadPlatformService=officeReadPlatformService;
		this.clientReadPlatformService=clientReadPlatformService;
		this.slabRateWritePlatformService = slabRateWritePlatformService;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
		this.clientBalanceWritePlatformService = clientBalanceWritePlatformService;
	}

	/* (non-Javadoc)
	 * @see #createOneTimeSale(org.mifosplatform.infrastructure.core.api.JsonCommand, java.lang.Long)
	 */
	@Transactional
	@Override
	public CommandProcessingResult createOneTimeSale(final JsonCommand command,final Long clientId) {

		try {
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromJsonHelper.parse(command.json());
			final Long itemId = command.longValueOfParameterNamed("itemId");
			ItemMaster item = this.itemMasterRepository.findOne(itemId);
			final Long quantity = command.longValueOfParameterNamed("quantity");

			// Check for Custome_Validation
			this.eventValidationReadPlatformService.checkForCustomValidations(clientId, "Rental",command.json(),getUserId());

			
			//This method is used to check the client has balance to activate prepaid service
			/*this.slabRateWritePlatformService.prepaidService(clientId ,item.getUnitPrice());*/

		   //end
		    ClientBillInfoData clientBillInfoData =this.clientBillInfoReadPlatformService.retrieveClientBillInfoDetails(clientId); 
		    BigDecimal conversionPrice = this.clientBalanceWritePlatformService.conversion(item.getCurrencyId(),clientBillInfoData.getBillCurrency(),item.getUnitPrice());

			final OneTimeSale oneTimeSale = OneTimeSale.fromJson(clientId, command,item,conversionPrice,clientBillInfoData.getBillCurrency());
			this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
			final List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveOnetimeSalesForInvoice(clientId);
			
			JsonObject jsonObject = new JsonObject();
			final String saleType = command.stringValueOfParameterNamed("saleType");
			if (saleType.equalsIgnoreCase("NEWSALE")) {
				for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
					CommandProcessingResult invoice=this.invoiceOneTimeSale.invoiceOneTimeSale(clientId,oneTimeSaleData,false);
					updateOneTimeSale(oneTimeSaleData,invoice);
				}
			}
			
			if(UnitEnumType.PIECES.toString().equalsIgnoreCase(item.getUnits())){
				
				JsonArray serialData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
				for (JsonElement je : serialData) {
					JsonObject serialNumber = je.getAsJsonObject();
					serialNumber.addProperty("clientId", oneTimeSale.getClientId());
					serialNumber.addProperty("orderId", oneTimeSale.getId());break;
				}
				jsonObject.addProperty("itemId", oneTimeSale.getItemId());
				jsonObject.addProperty("quantity", oneTimeSale.getQuantity());
				jsonObject.add("serialNumber", serialData);
				JsonCommand jsonCommand = new JsonCommand(null,jsonObject.toString(), element, fromJsonHelper, null, null,
						null, null, null, null, null, null, null, null, null, null);
				this.inventoryItemDetailsWritePlatformService.allocateHardware(jsonCommand);
			}else if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(item.getUnits()) || 
								UnitEnumType.METERS.toString().equalsIgnoreCase(item.getUnits())){
				
				final Collection<InventoryGrnData> grnDatas = this.grnReadPlatformService.retriveGrnIdswithItemId(itemId);
				for(InventoryGrnData grnData : grnDatas){
					InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(grnData.getId());
					if(inventoryGrn.getReceivedQuantity() > 0 && inventoryGrn.getStockQuantity() > 0){
						inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()-quantity);
						this.inventoryGrnRepository.save(inventoryGrn);
						break;
					}
				}
				//InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(command.longValueOfParameterNamed("grnId"));
				
			}else{
				throw new PlatformDataIntegrityException("error.msg.invalid.unit",
						"Unknown unit is mentioned");
			}
			
			return new CommandProcessingResult(Long.valueOf(oneTimeSale.getId()), clientId);
		} catch (final DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	 private void devicePairingFunctionality(JsonArray serialData,String isPairing,FromJsonHelper fromJsonHelper,Long clientId) {
		 String serialNo1 = null;String serialNo2 = null;JsonElement je = null;
		 String STBItemType = null;String VCItemType = null;String status = null;
		 Long serviceId = Long.valueOf(1);
		 ItemPairing itemPairing = null;
		 if("Y".equalsIgnoreCase(isPairing)){
			 for(int i=0;i<serialData.size();i++){
				 je = serialData.get(i);
				 status = this.fromJsonHelper.extractStringNamed("status", je);
				 if(i==0){
					 serialNo1 = this.fromJsonHelper.extractStringNamed("serialNumber", je);
					 STBItemType = this.fromJsonHelper.extractStringNamed("itemType", je);
				 }else if(i==1){
					 serialNo2 = this.fromJsonHelper.extractStringNamed("serialNumber", je);
					 VCItemType = this.fromJsonHelper.extractStringNamed("itemType", je);
				 }
			 }
			 if(serialNo1 != null && serialNo2 != null && STBItemType != null && VCItemType !=null){
				 itemPairing = new ItemPairing(clientId, serviceId, new Date(), null, status, serialNo1, STBItemType, serialNo2, VCItemType);
				 this.itemPairingRepository.saveAndFlush(itemPairing);
			 }
		 }
	 
	 }
		
	private Long getUserId() {
			Long userId=null;
			SecurityContext context = SecurityContextHolder.getContext();
				if(context.getAuthentication() != null){
					AppUser appUser=this.context.authenticatedUser();
					userId=appUser.getId();
				}else {
					userId=new Long(0);
				}
				
				return userId;
		}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		
		LOGGER.error(dve.getMessage(), dve);
		final Throwable realCause=dve.getMostSpecificCause();
		throw new PlatformDataIntegrityException(
				"error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "
						+ realCause.getMessage());
		

	}

	public void updateOneTimeSale(final OneTimeSaleData oneTimeSaleData,final CommandProcessingResult invoice) {

		OneTimeSale oneTimeSale = oneTimeSaleRepository.findOne(oneTimeSaleData.getId());
		oneTimeSale.setIsInvoiced('Y');
		oneTimeSale.setInvoiceId(invoice.resourceId());
		oneTimeSaleRepository.save(oneTimeSale);

	}

	/* (non-Javadoc)
	 * @see #calculatePrice(java.lang.Long, org.mifosplatform.infrastructure.core.api.JsonQuery)
	 */
	@Override
	public ItemData calculatePrice(final Long itemId, final JsonQuery query) {

		try {
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForPrice(query.parsedJson());
			final BigDecimal unitprice = fromJsonHelper.extractBigDecimalWithLocaleNamed("unitPrice",query.parsedJson());
			final String units = fromJsonHelper.extractStringNamed("units", query.parsedJson());
			BigDecimal itemprice = null;
			BigDecimal totalPrice = null;
			ItemMaster itemMaster = this.itemMasterRepository.findOne(itemId);
		
		    if (unitprice != null) {
				itemprice = unitprice;
			} else {
				itemprice = itemMaster.getUnitPrice();
			}
		    List<ItemData> itemCodeData = this.oneTimeSaleReadPlatformService.retrieveItemData();
			List<DiscountMasterData> discountdata = this.discountReadPlatformService.retrieveAllDiscounts();
			ItemData itemData = this.itemReadPlatformService.retrieveSingleItemDetails(null, itemId,null,false);
			itemData.setUnitPrice(itemprice);
			List<ChargesData> chargesDatas = this.itemReadPlatformService.retrieveChargeCode();

		    if(UnitEnumType.PIECES.toString().equalsIgnoreCase(units)){
		    	final Integer quantity = fromJsonHelper.extractIntegerWithLocaleNamed("quantity",query.parsedJson());
		    	totalPrice = itemprice.multiply(new BigDecimal(quantity));
		    	return new ItemData(itemCodeData, itemData, totalPrice, quantity.toString(), discountdata, chargesDatas, null);
		    }else{
		    	final String quantityValue = fromJsonHelper.extractStringNamed("quantity",query.parsedJson());
		    	totalPrice = itemprice.multiply(new BigDecimal(quantityValue));
		    	return new ItemData(itemCodeData, itemData, totalPrice, quantityValue, discountdata, chargesDatas, null);
		    }
			
			
		} catch (final DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return null;

		}
	}

	/* (non-Javadoc)
	 * @see #deleteOneTimeSale(org.mifosplatform.infrastructure.core.api.JsonCommand, java.lang.Long)
	 */
	@Transactional
	@Override
	public CommandProcessingResult deleteOneTimeSale(final Long entityId) {

		OneTimeSale oneTimeSale = null;
		try {
			oneTimeSale = this.findOneById(entityId);
		    if(oneTimeSale.getDeviceMode().equalsIgnoreCase("NEWSALE")&&oneTimeSale.getIsInvoiced()=='Y'){
				ChargeCodeMaster chargeCode=this.chargeCodeRepository.findOneByChargeCode(oneTimeSale.getChargeCode());
				if(oneTimeSale.getInvoiceId()!=null){//check for old onetimesale's
				   BillItem oldInvoice=this.billItemRepository.findOne(oneTimeSale.getInvoiceId());
				   List<Charge> charge=oldInvoice.getCharges();
				   BigDecimal discountAmount=charge.get(0).getDiscountAmount();
				   //cancel sale calling 
				   OneTimeSale cancelDeviceSale= new OneTimeSale(oneTimeSale.getClientId(), oneTimeSale.getItemId(), oneTimeSale.getUnits(),oneTimeSale.getQuantity(),oneTimeSale.getChargeCode(), oneTimeSale.getUnitPrice(), oneTimeSale.getTotalPrice(), 
			        		                           DateUtils.getLocalDateOfTenant(),oneTimeSale.getDiscountId(),oneTimeSale.getOfficeId(),CodeNameConstants.CODE_CANCEL_SALE,null,oneTimeSale.getClientServiceId(),null);
				   this.oneTimeSaleRepository.saveAndFlush(cancelDeviceSale);
				   OneTimeSaleData oneTimeSaleData = new OneTimeSaleData(cancelDeviceSale.getId(),oneTimeSale.getClientId(), oneTimeSale.getUnits(), oneTimeSale.getChargeCode(), 
						                             chargeCode.getChargeType(),oneTimeSale.getUnitPrice(),oneTimeSale.getQuantity(), oneTimeSale.getTotalPrice(), "Y",
						                             oneTimeSale.getItemId(),oneTimeSale.getDiscountId(),chargeCode.getTaxInclusive(),null,null,null);
				   CommandProcessingResult invoice=this.invoiceOneTimeSale.reverseInvoiceForOneTimeSale(oneTimeSale.getClientId(),oneTimeSaleData,discountAmount,false);
				   cancelDeviceSale.setIsDeleted('Y');
				   cancelDeviceSale.setInvoiceId(invoice.resourceId());
				   cancelDeviceSale.setIsInvoiced('Y');
				   this.oneTimeSaleRepository.save(cancelDeviceSale);
				   oldInvoice.setDueAmount(BigDecimal.ZERO);
				   this.billItemRepository.save(oldInvoice);
				  }
			 }
			oneTimeSale.setIsDeleted('Y');
			this.oneTimeSaleRepository.save(oneTimeSale);

		} catch (final DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
		}
		return new CommandProcessingResult(Long.valueOf(oneTimeSale.getId()),oneTimeSale.getClientId());
	}

	private OneTimeSale findOneById(final Long saleId) {
	
		try{
			OneTimeSale oneTimeSale=this.oneTimeSaleRepository.findOne(saleId);
			return oneTimeSale;
		}catch(Exception e){
			throw new DeviceSaleNotFoundException(saleId.toString());
		}
	}

/*	@Override
	public CommandProcessingResult createOneTimeSaleDevicePlan(final JsonCommand command,final Long clientId) {

		try {
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromJsonHelper.parse(command.json());
			final Long itemId = command.longValueOfParameterNamed("itemId");
			ItemMaster item = this.itemMasterRepository.findOne(itemId);
			final Long quantity = command.longValueOfParameterNamed("quantity");
			ItemDetails itemDetails = this.itemDetailsRepository.findOne(quantity);
			final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("serialNumber", element);
			
			if (commandArray != null) {
				for (JsonElement jsonelement : commandArray) {
					final Long orderId = fromApiJsonHelper.extractLongNamed("orderId", jsonelement);
					Order order = this.orderRepository.findOne(orderId);
					
					this.eventValidationReadPlatformService.checkForCustomValidations(clientId, "Rental",command.json(),getUserId());

					final OneTimeSale oneTimeSale = OneTimeSale.fromJson(clientId, command,item);
					this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
					final List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveOnetimeSalesForInvoice(clientId);
					

					//Order order = this.orderRepository.findOne(orderId);
					this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
					final List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveOnetimeSalesForInvoice(clientId);
					
					
					JsonObject jsonObject = new JsonObject();
					final String saleType = command.stringValueOfParameterNamed("saleType");
					if (saleType.equalsIgnoreCase("NEWSALE")) {
						for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
							CommandProcessingResult invoice=this.invoiceOneTimeSale.invoiceOneTimeSale(clientId,oneTimeSaleData,false);
							updateOneTimeSale(oneTimeSaleData,invoice);
						}
					}
					
					if(UnitEnumType.PIECES.toString().equalsIgnoreCase(item.getUnits())){
						
						JsonArray serialData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
						for (JsonElement je : serialData) {
							JsonObject serialNumber = je.getAsJsonObject();
							serialNumber.addProperty("clientId", order.getClientId());
							serialNumber.addProperty("orderId", order.getId());break;
						}
						jsonObject.addProperty("itemId", item.getId());
						jsonObject.addProperty("quantity", itemDetails.getReceivedQuantity());
						jsonObject.add("serialNumber", serialData);
						JsonCommand jsonCommand = new JsonCommand(null,jsonObject.toString(), element, fromJsonHelper, null, null,
								null, null, null, null, null, null, null, null, null, null);
						this.inventoryItemDetailsWritePlatformService.allocateHardware(jsonCommand);
					}else if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(item.getUnits()) || 
										UnitEnumType.METERS.toString().equalsIgnoreCase(item.getUnits())){
						
						final Collection<InventoryGrnData> grnDatas = this.grnReadPlatformService.retriveGrnIdswithItemId(itemId);
						for(InventoryGrnData grnData : grnDatas){
							InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(grnData.getId());
							if(inventoryGrn.getReceivedQuantity() > 0 && inventoryGrn.getStockQuantity() > 0){
								inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()-quantity);
								this.inventoryGrnRepository.save(inventoryGrn);
								break;
							}
						}
						//InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(command.longValueOfParameterNamed("grnId"));
						
					}else{
						throw new PlatformDataIntegrityException("error.msg.invalid.unit",
								"Unknown unit is mentioned");
					}
					
				}
			}
			
			
			//final Long orderId = element.longValueOfParameterNamed("orderId");
			//Order order = this.orderRepository.findOne(orderId);

			// Check for Custome_Validation
			this.eventValidationReadPlatformService.checkForCustomValidations(clientId, "Rental",command.json(),getUserId());

			final OneTimeSale oneTimeSale = OneTimeSale.fromJson(clientId, command,item);
			this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
			final List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveOnetimeSalesForInvoice(clientId);
			

			//Order order = this.orderRepository.findOne(orderId);
			this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
			final List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveOnetimeSalesForInvoice(clientId);
			
			
			JsonObject jsonObject = new JsonObject();
			final String saleType = command.stringValueOfParameterNamed("saleType");
			if (saleType.equalsIgnoreCase("NEWSALE")) {
				for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
					CommandProcessingResult invoice=this.invoiceOneTimeSale.invoiceOneTimeSale(clientId,oneTimeSaleData,false);
					updateOneTimeSale(oneTimeSaleData,invoice);
				}
			}
			
			if(UnitEnumType.PIECES.toString().equalsIgnoreCase(item.getUnits())){
				
				JsonArray serialData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
				for (JsonElement je : serialData) {
					JsonObject serialNumber = je.getAsJsonObject();
					serialNumber.addProperty("clientId", order.getClientId());
					serialNumber.addProperty("orderId", order.getId());break;
				}
				jsonObject.addProperty("itemId", item.getId());
				//jsonObject.addProperty("quantity", order.getQuantity());
				jsonObject.add("serialNumber", serialData);
				JsonCommand jsonCommand = new JsonCommand(null,jsonObject.toString(), element, fromJsonHelper, null, null,
						null, null, null, null, null, null, null, null, null, null);
				this.inventoryItemDetailsWritePlatformService.allocateHardware(jsonCommand);
			}else if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(item.getUnits()) || 
								UnitEnumType.METERS.toString().equalsIgnoreCase(item.getUnits())){
				
				final Collection<InventoryGrnData> grnDatas = this.grnReadPlatformService.retriveGrnIdswithItemId(itemId);
				for(InventoryGrnData grnData : grnDatas){
					InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(grnData.getId());
					if(inventoryGrn.getReceivedQuantity() > 0 && inventoryGrn.getStockQuantity() > 0){
						inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()-quantity);
						this.inventoryGrnRepository.save(inventoryGrn);
						break;
					}
				}
				//InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(command.longValueOfParameterNamed("grnId"));
				
			}else{
				throw new PlatformDataIntegrityException("error.msg.invalid.unit",
						"Unknown unit is mentioned");
			}
			
			return new CommandProcessingResult(Long.valueOf(order.getId()), clientId);
		} catch (final DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}*/

	
	
	
}