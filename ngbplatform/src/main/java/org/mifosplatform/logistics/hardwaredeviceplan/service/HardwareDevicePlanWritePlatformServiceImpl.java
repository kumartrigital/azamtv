package org.mifosplatform.logistics.hardwaredeviceplan.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.cms.eventorder.exception.InsufficientAmountException;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.grn.service.GrnReadPlatformService;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.domain.UnitEnumType;
import org.mifosplatform.logistics.itemdetails.data.AllocationHardwareData;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrn;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocation;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocationRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.serialization.InventoryItemAllocationCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsReadPlatformService;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistory;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistoryJpaRepository;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSale;
import org.mifosplatform.logistics.onetimesale.serialization.OneTimesaleCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformServiceImpl;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class HardwareDevicePlanWritePlatformServiceImpl implements HardwareDevicePlanWritePlatformService{

	private final static Logger logger =(Logger)  LoggerFactory.getLogger(HardwareDevicePlanWritePlatformServiceImpl.class);
	
	private final PlatformSecurityContext context;
	private final OneTimesaleCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final FromJsonHelper fromJsonHelper;
	private final ItemRepository itemMasterRepository;
	private final ItemDetailsRepository itemDetailsRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final OrderRepository orderRepository;
	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final EventValidationReadPlatformService eventValidationReadPlatformService;
	private final InventoryGrnRepository inventoryGrnRepository;
	private final GrnReadPlatformService grnReadPlatformService;
	private final InventoryItemAllocationCommandFromApiJsonDeserializer inventoryItemAllocationCommandFromApiJsonDeserializer;
	private final ItemRepository itemRepository;
	private final ItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService;
	private final ItemDetailsRepository inventoryItemDetailsRepository;
	private final ItemDetailsAllocationRepository inventoryItemDetailsAllocationRepository;
	private final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository;

	private final ConfigurationRepository configurationRepository;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final SlabRateWritePlatformService slabRateWritePlatformService;
	@Autowired
	public HardwareDevicePlanWritePlatformServiceImpl(PlatformSecurityContext context,
			OneTimesaleCommandFromApiJsonDeserializer apiJsonDeserializer, FromJsonHelper fromJsonHelper,
			ItemRepository itemMasterRepository, ItemDetailsRepository itemDetailsRepository,
			FromJsonHelper fromApiJsonHelper, OrderRepository orderRepository,
			ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,
			EventValidationReadPlatformService eventValidationReadPlatformService,
			InventoryGrnRepository inventoryGrnRepository, GrnReadPlatformService grnReadPlatformService,
			InventoryItemAllocationCommandFromApiJsonDeserializer inventoryItemAllocationCommandFromApiJsonDeserializer,
			ItemRepository itemRepository,ItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService,
			ItemDetailsRepository inventoryItemDetailsRepository,ItemDetailsAllocationRepository inventoryItemDetailsAllocationRepository,
			InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository,
			ConfigurationRepository configurationRepository,
			OfficeReadPlatformService officeReadPlatformService,
			ClientReadPlatformService clientReadPlatformService,final SlabRateWritePlatformService slabRateWritePlatformService) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.fromJsonHelper = fromJsonHelper;
		this.itemMasterRepository = itemMasterRepository;
		this.itemDetailsRepository = itemDetailsRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.orderRepository = orderRepository;
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
		this.eventValidationReadPlatformService = eventValidationReadPlatformService;
		this.inventoryGrnRepository = inventoryGrnRepository;
		this.grnReadPlatformService = grnReadPlatformService;
		this.inventoryItemAllocationCommandFromApiJsonDeserializer = inventoryItemAllocationCommandFromApiJsonDeserializer;
	    this.itemRepository = itemRepository;
	    this.inventoryItemDetailsReadPlatformService = inventoryItemDetailsReadPlatformService;
	    this.inventoryItemDetailsRepository = inventoryItemDetailsRepository;
	    this.inventoryItemDetailsAllocationRepository = inventoryItemDetailsAllocationRepository;
	    this.inventoryTransactionHistoryJpaRepository = inventoryTransactionHistoryJpaRepository;
	    this.configurationRepository=configurationRepository;
	    this.officeReadPlatformService=officeReadPlatformService;
	    this.clientReadPlatformService=clientReadPlatformService;
	    this.slabRateWritePlatformService = slabRateWritePlatformService;
	}

	@Override
	public CommandProcessingResult createHardwareDevicePlan(final JsonCommand command,final Long clientId) {

		try {
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromJsonHelper.parse(command.json());
			final Long itemId = command.longValueOfParameterNamed("itemId");
			ItemMaster item = this.itemMasterRepository.findOne(itemId);
			final Long quantity = command.longValueOfParameterNamed("quantity");
			final String saleType = command.stringValueOfParameterNamed("saleType");
			final Long discountId = command.longValueOfParameterNamed("discountId");
			//ItemDetails itemDetails = this.itemDetailsRepository.findOne(quantity);
			
			//This method is used to check the client has balance to activate prepaid service
			/*this.slabRateWritePlatformService.prepaidService(clientId ,item.getUnitPrice());*/
		    //end
			
			
			
			final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("serialNumber", element);
			
			if (commandArray != null) {
				for (JsonElement jsonelement : commandArray) {
					final Long clientid = fromApiJsonHelper.extractLongNamed("clientId", jsonelement);
					final Long resourceId = fromApiJsonHelper.extractLongNamed("resourceId", jsonelement);
					Order order = this.orderRepository.findOne(resourceId);
					
					this.eventValidationReadPlatformService.checkForCustomValidations(clientId, "Rental",command.json(),getUserId());

					
					JsonObject jsonObject = new JsonObject();
					/*final String saleType1 = command.stringValueOfParameterNamed("saleType");
					if (saleType.equalsIgnoreCase("HARDWARE")) {
						for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
							CommandProcessingResult invoice=this.invoiceOneTimeSale.invoiceOneTimeSale(clientId,oneTimeSaleData,false);
							updateOneTimeSale(oneTimeSaleData,invoice);
						}
					}*/
					
					if(UnitEnumType.PIECES.toString().equalsIgnoreCase(item.getUnits())){
						
						JsonArray serialData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
						for (JsonElement je : serialData) {
							JsonObject serialNumber = je.getAsJsonObject();
							serialNumber.addProperty("clientId", order.getClientId());
							serialNumber.addProperty("orderId", order.getId());break;
						}
						jsonObject.addProperty("itemId", item.getId());
						jsonObject.addProperty("quantity", quantity);
						jsonObject.add("serialNumber", serialData);
						jsonObject.addProperty("saleType",saleType);
						jsonObject.addProperty("discountId", discountId);
						JsonCommand jsonCommand = new JsonCommand(null,jsonObject.toString(), element, fromJsonHelper, null, null,
								null, null, null, null, null, null, null, null, null, null);
						this.inventoryItemDetailsWritePlatformService.allocateHardware(jsonCommand);
						//this.allocateHardwareDevice(jsonCommand);
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
			
			return new CommandProcessingResult(clientId);
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	/*@Override
	public CommandProcessingResult allocateHardwareDevice(final JsonCommand jsonCommand) {

		try{
			
			this.context.authenticatedUser();
			 Long clientId=null;
			 Long entityId=null;
			inventoryItemAllocationCommandFromApiJsonDeserializer.validateForCreate(jsonCommand.json());
			final JsonElement element = fromJsonHelper.parse(jsonCommand.json());
			JsonArray allocationData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
			//ItemMaster itemMasterData=this.itemRepository.findOne(command.longValueOfParameterNamed("itemMasterId"));
			ItemMaster itemMasterData=this.itemRepository.findOne(fromJsonHelper.extractLongNamed("itemId", element));
			//int i=1;
				for(JsonElement j:allocationData){
		        	
					ItemDetailsAllocation inventoryItemDetailsAllocation = ItemDetailsAllocation.fromJson(j,fromJsonHelper);
					Order order = this.orderRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
					inventoryItemDetailsAllocation.setClientServiceId(order.getClientServiceId());
					inventoryItemDetailsAllocation.setOrderType("HARDWARE");
					AllocationHardwareData allocationHardwareData = inventoryItemDetailsReadPlatformService.retriveInventoryItemDetail(inventoryItemDetailsAllocation.getSerialNumber());
		        	checkHardwareCondition(allocationHardwareData);
		        	ItemDetails inventoryItemDetails = inventoryItemDetailsRepository.findOne(allocationHardwareData.getItemDetailsId());
					inventoryItemDetails.setClientId(inventoryItemDetailsAllocation.getClientId());
					inventoryItemDetails.setStatus("In Use");
					this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
					this.inventoryItemDetailsAllocationRepository.saveAndFlush(inventoryItemDetailsAllocation);
					InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
					if(inventoryGrn.getReceivedQuantity() > 0 && inventoryGrn.getStockQuantity() > 0){
						inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()-1);
						this.inventoryGrnRepository.saveAndFlush(inventoryGrn);
					}
					InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(DateUtils.getDateOfTenant(), 
							order.getId(),"Allocation",inventoryItemDetailsAllocation.getSerialNumber(), inventoryItemDetailsAllocation.getItemMasterId(),
							inventoryItemDetails.getOfficeId(),inventoryItemDetailsAllocation.getClientId());
					
					this.inventoryTransactionHistoryJpaRepository.save(transactionHistory);
					inventoryItemDetailsAllocation.getId();
				}
				return new CommandProcessingResult(entityId,clientId);
		
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(jsonCommand, dve); 
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}*/
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        Throwable realCause = dve.getMostSpecificCause();
       if (realCause.getMessage().contains("serial_no_constraint")){
       	final String serialNumber=command.stringValueOfParameterNamed("serialNumber");
       	throw new PlatformDataIntegrityException("validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber",serialNumber);
       	
       }

       logger.error(dve.getMessage(), dve);   	
}

	/*private void checkHardwareCondition(AllocationHardwareData allocationHardwareData) {
		
		if(allocationHardwareData == null){
			throw new PlatformDataIntegrityException("invalid.serial.no", "invalid.serial.no","serialNumber");
		}
		
		if(!allocationHardwareData.getQuality().equalsIgnoreCase("Good") || !allocationHardwareData.getQuality().equalsIgnoreCase("Good")){
			throw new PlatformDataIntegrityException("product.not.in.good.condition", "product.not.in.good.condition","product.not.in.good.condition");
		}
									
		if(allocationHardwareData.getClientId()!=null && allocationHardwareData.getClientId()!=0){
			
			if(allocationHardwareData.getClientId()>0){
				throw new PlatformDataIntegrityException("SerialNumber "+allocationHardwareData.getSerialNumber()+" already allocated.", 
						                "SerialNumber "+allocationHardwareData.getSerialNumber()+ "already allocated.","serialNumber"+allocationHardwareData.getSerialNumber());	
			}}
		}*/
	
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
	
}
