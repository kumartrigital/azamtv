package org.mifosplatform.logistics.itemdetails.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Synchronize;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.finance.payments.domain.ChequePayment;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.domain.UnitEnumType;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.itemdetails.api.ItemDetailsApiResource;
import org.mifosplatform.logistics.itemdetails.data.AllocationHardwareData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrn;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocation;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocationRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.logistics.itemdetails.exception.OrderQuantityExceedsException;
import org.mifosplatform.logistics.itemdetails.serialization.InventoryItemAllocationCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.itemdetails.serialization.InventoryItemCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistory;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistoryJpaRepository;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairing;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairingRepository;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSale;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSaleRepository;
import org.mifosplatform.logistics.onetimesale.service.InvoiceOneTimeSale;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
/*import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;*/
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.exceptions.NoGrnIdFoundException;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMapping;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMappingRepository;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestDetail;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestRepository;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisionActions;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisioningActionsRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.workflow.eventactionmapping.exception.EventActionMappingNotFoundException;
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



@Service
public class ItemDetailsWritePlatformServiceImp implements ItemDetailsWritePlatformService{
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(ItemDetailsWritePlatformServiceImp.class);
	private final FromJsonHelper fromJsonHelper;
	private final PlatformSecurityContext context;
	private final ItemRepository itemRepository;
	private final OneTimeSaleRepository oneTimeSaleRepository;
	private final InventoryGrnRepository inventoryGrnRepository;
	private final ConfigurationRepository configurationRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	private final ItemDetailsRepository inventoryItemDetailsRepository;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	/*private final HardwareAssociationReadplatformService associationReadplatformService;*/
	private final ItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService;
	private final EventValidationReadPlatformService eventValidationReadPlatformService;
	/*private final HardwareAssociationWriteplatformService associationWriteplatformService;*/
	private final ProvisioningActionsRepository provisioningActionsRepository;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;
	private final ItemDetailsAllocationRepository inventoryItemDetailsAllocationRepository; 
	private final PropertyDeviceMappingRepository propertyDeviceMappingRepository;
	private final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository;
	private final InventoryItemCommandFromApiJsonDeserializer inventoryItemCommandFromApiJsonDeserializer;
	private final InventoryItemAllocationCommandFromApiJsonDeserializer inventoryItemAllocationCommandFromApiJsonDeserializer;
	private final ItemDetailsAllocationRepository allocationRepository;
	private final ItemRepository itemMasterRepository;
	private final ItemPairingRepository itemPairingRepository;
	private final OrderRepository orderRepository;
	private final ProvisioningRequestRepository provisioningRequestRepository;
	private final ItemDetailsApiResource itemDetailsApiResource;
	private final ClientRepository clientRepository;
	private final ClientServiceRepository clientServiceRepository;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	private final FromJsonHelper fromApiJsonHelper;
	private final CrmServices crmServices;
	private final InvoiceOneTimeSale invoiceOneTimeSale;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final ClientBalanceWritePlatformService clientBalanceWritePlatformService;
	
	@Autowired
	public ItemDetailsWritePlatformServiceImp(final ItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService, 
			final PlatformSecurityContext context, final InventoryGrnRepository inventoryitemRopository,
			final InventoryItemCommandFromApiJsonDeserializer inventoryItemCommandFromApiJsonDeserializer,
			final InventoryItemAllocationCommandFromApiJsonDeserializer inventoryItemAllocationCommandFromApiJsonDeserializer, 
			final ItemDetailsAllocationRepository inventoryItemDetailsAllocationRepository,final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService, 
			final OneTimeSaleRepository oneTimeSaleRepository,final ItemDetailsRepository inventoryItemDetailsRepository,final FromJsonHelper fromJsonHelper, 
			final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository,final ConfigurationRepository  configurationRepository,
			/*final HardwareAssociationReadplatformService associationReadplatformService,final HardwareAssociationWriteplatformService associationWriteplatformService,*/
			final ItemRepository itemRepository,final OrderReadPlatformService orderReadPlatformService,
			final ProvisioningWritePlatformService provisioningWritePlatformService,final EventValidationReadPlatformService eventValidationReadPlatformService,
			final ProvisioningActionsRepository provisioningActionsRepository,final PropertyDeviceMappingRepository propertyDeviceMappingRepository, 
			final ItemDetailsAllocationRepository allocationRepository, final ItemRepository itemMasterRepository,
			final ItemPairingRepository itemPairingRepository, final OrderRepository orderRepository, final ProvisioningRequestRepository provisioningRequestRepository,
			final ItemDetailsApiResource itemDetailsApiResource, final ClientRepository clientRepository,
			final ClientServiceRepository clientServiceRepository,
			final CodeValueReadPlatformService codeValueReadPlatformService,final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final FromJsonHelper fromApiJsonHelper, final CrmServices crmServices,final InvoiceOneTimeSale invoiceOneTimeSale,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService,final ClientBalanceWritePlatformService clientBalanceWritePlatformService){
		
		this.inventoryItemDetailsReadPlatformService = inventoryItemDetailsReadPlatformService;
		this.context=context;
		this.inventoryItemDetailsRepository=inventoryItemDetailsRepository;
		this.inventoryGrnRepository=inventoryitemRopository;
		this.inventoryItemCommandFromApiJsonDeserializer = inventoryItemCommandFromApiJsonDeserializer;
		this.inventoryItemAllocationCommandFromApiJsonDeserializer = inventoryItemAllocationCommandFromApiJsonDeserializer;
		this.inventoryItemDetailsAllocationRepository = inventoryItemDetailsAllocationRepository;
		this.oneTimeSaleReadPlatformService=oneTimeSaleReadPlatformService;
		this.oneTimeSaleRepository = oneTimeSaleRepository;
		this.fromJsonHelper=fromJsonHelper;
		this.provisioningActionsRepository=provisioningActionsRepository;
		this.inventoryTransactionHistoryJpaRepository = inventoryTransactionHistoryJpaRepository;
		this.configurationRepository=configurationRepository;
		/*this.associationReadplatformService=associationReadplatformService;
		this.associationWriteplatformService=associationWriteplatformService;*/
		this.propertyDeviceMappingRepository = propertyDeviceMappingRepository;
		this.itemRepository=itemRepository;
		this.orderReadPlatformService=orderReadPlatformService;
		this.provisioningWritePlatformService=provisioningWritePlatformService;
		this.eventValidationReadPlatformService=eventValidationReadPlatformService;
		this.allocationRepository = allocationRepository;
		this.itemMasterRepository = itemMasterRepository;
		this.itemPairingRepository = itemPairingRepository;
		this.orderRepository = orderRepository;
		this.provisioningRequestRepository = provisioningRequestRepository;
		this.itemDetailsApiResource = itemDetailsApiResource;
		this.clientRepository = clientRepository;
		this.clientServiceRepository = clientServiceRepository;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.crmServices=crmServices;
		this.invoiceOneTimeSale = invoiceOneTimeSale;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
		this.clientBalanceWritePlatformService = clientBalanceWritePlatformService;
	}
	

	
	
	

	@Override
	public synchronized CommandProcessingResult addItem(final JsonCommand command,Long flag) {

		try{
			context.authenticatedUser();
			ItemDetails inventoryItemDetails=null;			
			inventoryItemCommandFromApiJsonDeserializer.validateForCreate(command);
			final Configuration configuration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_ITEM_MODEL_ENABLE);
			if(configuration.isEnabled()){
		      String configurations  = configuration.getValue();
			  JSONArray configurationArray = new JSONArray(configurations);
			  JSONObject configurationObject = null;
			  int length = command.stringValueOfParameterName("serialNumber").length();
			  for(int i=0;i<configurationArray.length();i++){
				  configurationObject = configurationArray.getJSONObject(i);
				 if(configurationObject.optString("itemModel").equalsIgnoreCase(command.stringValueOfParameterName("itemModelName"))&&
						 configurationObject.optString("itemType").equalsIgnoreCase(command.stringValueOfParameterName("itemCode")) ){
					 if(length!=configurationObject.optInt("Length")){
						 throw new PlatformDataIntegrityException("Please Enter The Correct Length As Defined To The Item Model", "Invalid Length ofserial number", "Invalid Length ofserial number");
					 }
				 }
			  }
		      /*configurations
		      for(String itemModelValue : itemModelValues ){
				  
			  }*/
		      
			}
			Long item = command.longValueOfParameterNamed("itemMasterId");
			ItemMaster items = this.itemMasterRepository.findOne(item);
			Boolean isSerialRequired = true;
			Long quantity = command.longValueOfParameterNamed("quantity");
			if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(items.getUnits()) ||
					UnitEnumType.METERS.toString().equalsIgnoreCase(items.getUnits())){
				isSerialRequired = false;
			}
			
			inventoryItemCommandFromApiJsonDeserializer.validateForSerialNumber(command.json(), isSerialRequired);
			inventoryItemDetails = ItemDetails.fromJson(command,fromJsonHelper, isSerialRequired);
			ItemMaster itemMaster=this.itemRepository.findOne(command.longValueOfParameterNamed("itemMasterId"));
			InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(command.longValueOfParameterNamed("grnId"));
			
			if(itemMaster == null){
				throw new ItemNotFoundException(command.longValueOfParameterNamed("itemMasterId").toString());
			}
			if(inventoryGrn != null&& inventoryGrn.getItemMasterId().equals(itemMaster.getId())){
			 if(itemMaster != null) {
				if(itemMaster.getWarranty() != null){
					LocalDate warrantyEndDate = DateUtils.getLocalDateOfTenant().plusMonths(itemMaster.getWarranty().intValue()).minusDays(1);
					inventoryItemDetails.setWarrantyDate(warrantyEndDate);
				}
			 }
			if(inventoryGrn != null){
				inventoryItemDetails.setOfficeId(inventoryGrn.getOfficeId());
				inventoryItemDetails.setLocationId(inventoryGrn.getOfficeId());

				if(inventoryGrn.getReceivedQuantity() < inventoryGrn.getOrderdQuantity()){
					if(UnitEnumType.PIECES.toString().equalsIgnoreCase(items.getUnits())){
						inventoryGrn.setReceivedQuantity(inventoryGrn.getReceivedQuantity()+1);
						inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()+1);
					}else if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(items.getUnits()) ||
								UnitEnumType.METERS.toString().equalsIgnoreCase(items.getUnits())){
						inventoryGrn.setReceivedQuantity(inventoryGrn.getReceivedQuantity()+quantity);
						inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()+quantity);
					}
				
				}
				else{
					throw new OrderQuantityExceedsException(inventoryGrn.getOrderdQuantity());
				}
			
			}
			this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
			this.inventoryGrnRepository.saveAndFlush(inventoryGrn);
			
			return new CommandProcessingResultBuilder().withEntityId(inventoryItemDetails.getId()).build();
			}
			else{
				throw new NoGrnIdFoundException(inventoryItemDetails.getGrnId());
			}
			//InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(), inventoryItemDetails.getId(),"Item Detail",inventoryItemDetails.getSerialNumber(), inventoryItemDetails.getItemMasterId(), inventoryItemDetails.getGrnId(), inventoryGrn.getOfficeId());
			//InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(),inventoryItemDetails.getId(),"Item Detail",inventoryItemDetails.getSerialNumber(),inventoryGrn.getOfficeId(),inventoryItemDetails.getClientId(),inventoryItemDetails.getItemMasterId());
			//inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			/*++processRecords;
             processStatus="Processed";*/
			
			
		} catch (DataIntegrityViolationException dve){
			
			handleDataIntegrityIssues(command,dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new PlatformDataIntegrityException("Configuration value in JSON is not formed Correctly", "", "");
		}
	}
	
		private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
	         Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("serial_no_constraint")){
	        	final String serialNumber=command.stringValueOfParameterNamed("serialNumber");
	        	throw new PlatformDataIntegrityException("validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber",serialNumber);
	        	
	        }

	        logger.error(dve.getMessage(), dve);   	
	}
		@Transactional
		@Override
		public CommandProcessingResult updateItem(Long id,JsonCommand command)
		{
	        try{
	        	  
	        	this.context.authenticatedUser();
	        	ItemDetails inventoryItemDetails=ItemretrieveById(id);
	        	Long item = inventoryItemDetails.getItemMasterId();
				ItemMaster items = this.itemMasterRepository.findOne(item);
				Boolean isSerialRequired = true;
				if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(items.getUnits()) ||
						UnitEnumType.METERS.toString().equalsIgnoreCase(items.getUnits())){
					isSerialRequired = false;
				}
	        	this.inventoryItemCommandFromApiJsonDeserializer.validateForUpdate(command.json(), isSerialRequired);
	        	Long newQuantity = command.longValueOfParameterNamed("quantity");
	        	final String oldHardware =inventoryItemDetails.getProvisioningSerialNumber();
	        	final String oldSerilaNumber =inventoryItemDetails.getSerialNumber();
	        	final Long oldQuantity = inventoryItemDetails.getReceivedQuantity();
	        	final Map<String, Object> changes = inventoryItemDetails.update(command); 
	        	
	        	if(!changes.isEmpty()){
	        		this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
	        	}
	        	if(newQuantity != null && UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(items.getUnits()) ||
						UnitEnumType.METERS.toString().equalsIgnoreCase(items.getUnits())){
					
	        		InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
	        		if(newQuantity != oldQuantity){
	        			if(newQuantity <= inventoryGrn.getStockQuantity()){
	        				throw new OrderQuantityExceedsException();
	        			}
	        			
	        			inventoryGrn.setReceivedQuantity((inventoryGrn.getReceivedQuantity()-oldQuantity)+newQuantity);
		        		inventoryGrn.setStockQuantity((inventoryGrn.getStockQuantity()-oldQuantity)+newQuantity);
		        		// need to handle exception if stock is 0
		        		this.inventoryGrnRepository.save(inventoryGrn);
	        			
	        		}
				}
	        	
	        	if(UnitEnumType.PIECES.toString().equalsIgnoreCase(items.getUnits()) && !oldSerilaNumber.equalsIgnoreCase(inventoryItemDetails.getSerialNumber()) &&inventoryItemDetails.getClientId()!=null){
	        		
	        		ItemDetailsAllocation allocationData = this.inventoryItemDetailsAllocationRepository.findAllocatedDevicesBySerialNum(inventoryItemDetails.getClientId(),oldSerilaNumber);
		        		if(allocationData != null){
		        			allocationData.setSerialNumber(inventoryItemDetails.getSerialNumber());
		        			this.inventoryItemDetailsAllocationRepository.saveAndFlush(allocationData);
		        		}
	        	}
	        	
	        	if(UnitEnumType.PIECES.toString().equalsIgnoreCase(items.getUnits()) && (!oldHardware.equalsIgnoreCase(inventoryItemDetails.getProvisioningSerialNumber()) || !oldSerilaNumber.equalsIgnoreCase(inventoryItemDetails.getSerialNumber())) 
	        				&&inventoryItemDetails.getClientId()!=null){
	          	  
	        		this.provisioningWritePlatformService.updateHardwareDetails(inventoryItemDetails.getClientId(),inventoryItemDetails.getSerialNumber(),oldSerilaNumber,
	        				inventoryItemDetails .getProvisioningSerialNumber(),oldHardware);
	        	}
	         return new CommandProcessingResultBuilder().withEntityId(inventoryItemDetails.getId()).build();
	        	
	        }
	        catch(DataIntegrityViolationException dve){
	        	
	        	 if(dve.getCause()instanceof ConstraintViolationException){
	        		 handleDataIntegrityIssues(command, dve);
	        	 }
	        	 return CommandProcessingResult.empty(); 
	        }
	        
	}
		private ItemDetails ItemretrieveById(Long id) {
            
			ItemDetails itemId=this.inventoryItemDetailsRepository.findOne(id);
	              if (itemId== null) { throw new EventActionMappingNotFoundException(id.toString()); }
		          return itemId;	
		}

		@Transactional
		@Override
		public CommandProcessingResult  allocateHardware(JsonCommand command) {

			try{
				
				this.context.authenticatedUser();
				 Long clientId=null;
				 Long entityId=null;
				inventoryItemAllocationCommandFromApiJsonDeserializer.validateForCreate(command.json());
				final JsonElement element = fromJsonHelper.parse(command.json());
				Long discountId = fromJsonHelper.extractLongNamed("discountId", element);
				JsonArray allocationData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
				//ItemMaster itemMasterData=this.itemRepository.findOne(command.longValueOfParameterNamed("itemMasterId"));
				ItemMaster itemMasterData=this.itemRepository.findOne(fromJsonHelper.extractLongNamed("itemId", element));
				//int i=1;
					for(JsonElement j:allocationData){
			        	
						final String saleType = fromApiJsonHelper.extractStringNamed("saleType", j);
						
						if(saleType.equals("HARDWARE") ){
							
							ItemDetailsAllocation inventoryItemDetailsAllocation = ItemDetailsAllocation.fromJson(j,fromJsonHelper);
							Order order = this.orderRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
							//Order order = this.orderRepository.findOrderByClientId(inventoryItemDetailsAllocation.getClientId());
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

							final List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveItemDetailsForInvoice(inventoryItemDetailsAllocation.getClientId(),inventoryItemDetailsAllocation.getSerialNumber());
							for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
								oneTimeSaleData.setDiscountId(discountId);
								ClientBillInfoData clientBillInfoData =this.clientBillInfoReadPlatformService.retrieveClientBillInfoDetails(inventoryItemDetailsAllocation.getClientId()); 
							    BigDecimal conversionPrice = this.clientBalanceWritePlatformService.conversion(Long.valueOf(oneTimeSaleData.getBaseCurrencyId()),clientBillInfoData.getBillCurrency(),oneTimeSaleData.getTotalBasePrice());
							    oneTimeSaleData.setCurrencyId(clientBillInfoData.getBillCurrency());
							    oneTimeSaleData.setTotalPrice(conversionPrice);
								CommandProcessingResult invoice=this.invoiceOneTimeSale.invoiceOneTimeSale(inventoryItemDetailsAllocation.getClientId(),oneTimeSaleData,false);
							}
							
							
						}
						else{
						ItemDetailsAllocation inventoryItemDetailsAllocation = ItemDetailsAllocation.fromJson(j,fromJsonHelper);
						OneTimeSale oneTimeSale = this.oneTimeSaleRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
						//OneTimeSale oneTimeSale = this.oneTimeSaleRepository.findOneTimeSaleByClientIdAndItemMasterId(inventoryItemDetailsAllocation.getOrderId(),inventoryItemDetailsAllocation.getItemMasterId());
						inventoryItemDetailsAllocation.setClientServiceId(oneTimeSale.getClientServiceId());
						inventoryItemDetailsAllocation.setOrderType(oneTimeSale.getDeviceMode());
						AllocationHardwareData allocationHardwareData = inventoryItemDetailsReadPlatformService.retriveInventoryItemDetail(inventoryItemDetailsAllocation.getSerialNumber());
			        	checkHardwareCondition(allocationHardwareData);
			        	ItemDetails inventoryItemDetails = inventoryItemDetailsRepository.findOne(allocationHardwareData.getItemDetailsId());
						/*inventoryItemDetails.setItemMasterId(inventoryItemDetailsAllocation.getItemMasterId());*/
						inventoryItemDetails.setClientId(inventoryItemDetailsAllocation.getClientId());
						inventoryItemDetails.setStatus("In Use");
						LocalDate warrantyEndDate = new LocalDate(oneTimeSale.getSaleDate()).plusMonths(itemMasterData.getWarranty().intValue()).minusDays(1);
						inventoryItemDetails.setWarrantyDate(warrantyEndDate);
						this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
						this.inventoryItemDetailsAllocationRepository.saveAndFlush(inventoryItemDetailsAllocation);
						InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
						if(inventoryGrn.getReceivedQuantity() > 0 && inventoryGrn.getStockQuantity() > 0){
							inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()-1);
							this.inventoryGrnRepository.saveAndFlush(inventoryGrn);
						}
						
						oneTimeSale.setHardwareAllocated("ALLOCATED");
						this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
						clientId=oneTimeSale.getClientId();
						entityId=oneTimeSale.getId();

						InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(DateUtils.getDateOfTenant(), 
								oneTimeSale.getId(),"Allocation",inventoryItemDetailsAllocation.getSerialNumber(), inventoryItemDetailsAllocation.getItemMasterId(),
								inventoryItemDetails.getOfficeId(),inventoryItemDetailsAllocation.getClientId());
						
						this.inventoryTransactionHistoryJpaRepository.save(transactionHistory);
						inventoryItemDetailsAllocation.getId();
						}
					//	i++;
						
						  //For Plan And HardWare Association
						/*Configuration configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IMPLICIT_ASSOCIATION);
						
						if(configurationProperty.isEnabled()){
								ItemMaster itemMaster=this.itemRepository.findOne(inventoryItemDetails.getItemMasterId());
								List<HardwareAssociationData> allocationDetailsDatas=this.associationReadplatformService.retrieveClientAllocatedPlan(oneTimeSale.getClientId(),itemMaster.getItemCode());						    		   
								if(!allocationDetailsDatas.isEmpty()){
									
									this.associationWriteplatformService.createNewHardwareAssociation(oneTimeSale.getClientId(),
											allocationDetailsDatas.get(0).getPlanId(),inventoryItemDetails.getSerialNumber(),
											allocationDetailsDatas.get(0).getorderId(),"ALLOT");
						    		   }	
						    	}*/	
					}
					return new CommandProcessingResult(entityId,clientId);
			
			}catch(DataIntegrityViolationException dve){
				handleDataIntegrityIssues(command, dve); 
					return new CommandProcessingResult(Long.valueOf(-1));
			}
			
		}
		
		
		private void checkHardwareCondition(AllocationHardwareData allocationHardwareData) {
			
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
			}

		@Override
		public ItemDetailsAllocation deAllocateHardware(String serialNo,Long clientId, String remarks) {
				try{
					AllocationDetailsData allocationDetailsData=this.oneTimeSaleReadPlatformService.retrieveAllocationDetailsBySerialNo(serialNo);
					ItemDetailsAllocation inventoryItemDetailsAllocation=null;
					
						if(allocationDetailsData!=null){
							inventoryItemDetailsAllocation =this.inventoryItemDetailsAllocationRepository.findOne(allocationDetailsData.getId());
							inventoryItemDetailsAllocation.deAllocate(remarks);
							this.inventoryItemDetailsAllocationRepository.save(inventoryItemDetailsAllocation);
							ItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.findOne(allocationDetailsData.getItemDetailId());
							inventoryItemDetails.setAvailable();
							this.inventoryItemDetailsRepository.save(inventoryItemDetails);
							InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
							inventoryGrn.setStockQuantity(inventoryGrn.getStockQuantity()+1);
							this.inventoryGrnRepository.saveAndFlush(inventoryGrn);
							
							InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(), 
					  			inventoryItemDetailsAllocation.getOrderId(),"De Allocation",inventoryItemDetailsAllocation.getSerialNumber(), inventoryItemDetailsAllocation.getItemMasterId(),
								inventoryItemDetailsAllocation.getClientId(),inventoryItemDetails.getOfficeId());
							inventoryTransactionHistoryJpaRepository.save(transactionHistory);
					   
						}
						
				   return inventoryItemDetailsAllocation;
			
				}catch(DataIntegrityViolationException exception){
					handleDataIntegrityIssues(null, exception);
					return null;
				}
		}

		@Override
		public CommandProcessingResult deAllocateHardware(JsonCommand command) {
			

           try{
        	   
        	  final  String serialNo=command.stringValueOfParameterNamed("serialNo");
        	  final Long clientId=command.longValueOfParameterNamed("clientId");
        	  final String clientServiceStatus=command.stringValueOfParameterNamed("clientServiceStatus");

        	  if(command.hasParameter("clientServiceStatus")){
        	  this.checkServicestatusValidations(clientServiceStatus);
        	  }
		        //Check for Custome_Validation
				this.eventValidationReadPlatformService.checkForCustomValidations(clientId,"UnPairing", command.json(),getUserId());
        	   final Long activeorders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId,serialNo,null);
        	   	if(activeorders!= 0){
        	   		throw new ActivePlansFoundException();
        	   	}
        	   
        	   /*	List<AssociationData> associationDatas=this.associationReadplatformService.retrieveClientAssociationDetails(clientId);
        	   	for(AssociationData associationData:associationDatas ){
        	   		this.associationWriteplatformService.deAssociationHardware(associationData.getId());
        	   	}*/
        	   String remarks= command.stringValueOfParameterName("remark");
         	   ItemDetailsAllocation inventoryItemDetailsAllocation=this.deAllocateHardware(serialNo, clientId, remarks);
        	   
        	   /*List<ItemDetailsAllocation> allocations=this.allocationRepository.findRemainingAllocatedDevice(clientId,inventoryItemDetailsAllocation.getOrderId());
        	   if(allocations.isEmpty()){
        	      OneTimeSale oneTimeSale=this.oneTimeSaleRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
        	      oneTimeSale.setStatus();
        	      this.oneTimeSaleRepository.save(oneTimeSale);
        	   }*/
        	   if(inventoryItemDetailsAllocation.getOrderType().equals("NEWSALE")){
	        	   List<ItemDetailsAllocation> allocations=this.allocationRepository.findRemainingAllocatedDevice(clientId,inventoryItemDetailsAllocation.getOrderId());
	        	   if(allocations.isEmpty()){
	        	      OneTimeSale oneTimeSale=this.oneTimeSaleRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
	        	      oneTimeSale.setStatus();
	        	      this.oneTimeSaleRepository.save(oneTimeSale);
	        	   }
        	   }else{
        		   List<ItemDetailsAllocation> allocations=this.allocationRepository.findRemainingAllocatedDevice(clientId,inventoryItemDetailsAllocation.getOrderId());
	        	   if(allocations.isEmpty()){
	        	      Order order=this.orderRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
	        	      //order.setStatus("UNALLOCATED");
	        	      this.orderRepository.save(order);
	        	   }
        	   }
  			 Configuration globalConfiguration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_PROPERTY_MASTER);
  			 
  			 if(globalConfiguration.isEnabled()){
  				 
  				 PropertyDeviceMapping deviceMapping = this.propertyDeviceMappingRepository.findBySerailNumber(serialNo);
  				 if(deviceMapping != null){

				// PropertyMaster propertyMaster = this.propertyMasterRepository.findoneByPropertyCode(deviceMapping.getPropertyCode());
  				 deviceMapping.delete();
  				 this.propertyDeviceMappingRepository.save(deviceMapping);
  				   /*if(propertyMaster != null){
  					 PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_UNMAPPED,clientId,propertyMaster.getPropertyCode());
  				 	this.propertyHistoryRepository.save(propertyHistory);
  				   }*/
  				 }

  			 }
        	   ProvisionActions provisionActions=this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_RELEASE_DEVICE);
               if(provisionActions != null && provisionActions.isEnable() == 'Y'){
   				
   				this.provisioningWritePlatformService.postDetailsForProvisioning(clientId,Long.valueOf(0),ProvisioningApiConstants.REQUEST_RELEASE_DEVICE,
   						               provisionActions.getProvisioningSystem(),serialNo);
   			}       	   
            
               
             if(command.booleanPrimitiveValueOfParameterNamed("isStraight")) {this.releseDeviceFormParing(serialNo);}
               
               
        	   return new CommandProcessingResult(command.entityId(),clientId);
           }catch(DataIntegrityViolationException exception){
        	   
        	   return new CommandProcessingResult(Long.valueOf(-1));
           }
		}
		
		
		
		private void releseDeviceFormParing(String serialNo) {
		
			 ItemPairing itemPairing = this.itemPairingRepository.findOneBySerialNo(serialNo);
	      	    if(itemPairing == null){
	      	    	itemPairing = this.itemPairingRepository.findOneBySerialNo2(serialNo);
	      	    } 
	      	    if(itemPairing !=null){
	      	    	itemPairing.delete();
	      	    		this.itemPairingRepository.saveAndFlush(itemPairing);
	      	    }
		}

		private void checkServicestatusValidations(String Status){
			if(!(Status.equalsIgnoreCase("Terminated")|| Status.equalsIgnoreCase("New"))){
				
				throw new PlatformDataIntegrityException("device is not deallocated,only clientservicestatus is Terminated or New state", 
						"error.msg.device.not.deallocated.onlystatus.Terminated.New", null,Status);
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
		 
		@Transactional
		@Override
		public CommandProcessingResult deleteItem(Long id,JsonCommand command)
		{
	        try{
	        	this.context.authenticatedUser();
	        	ItemDetails inventoryItemDetails=ItemretrieveById(id);
	        	Long quantity = inventoryItemDetails.getReceivedQuantity();
	        	Long item = inventoryItemDetails.getItemMasterId();
				ItemMaster items = this.itemMasterRepository.findOne(item);
				
	        	InventoryGrn grn=this.inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
	        	inventoryItemDetails.itemDelete();
	        	this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
	        	if(UnitEnumType.ACCESSORIES.toString().equalsIgnoreCase(items.getUnits()) ||
						UnitEnumType.METERS.toString().equalsIgnoreCase(items.getUnits())){
	        		if(inventoryItemDetails.getReceivedQuantity() > grn.getStockQuantity()){
	        			throw new OrderQuantityExceedsException("Unable to delete");
	        		}
	        		grn.setReceivedQuantity(grn.getReceivedQuantity()-quantity);
	        		grn.setStockQuantity(grn.getStockQuantity()-quantity);
	        		// handle if stock is 0
				}else{
					Long ReceivedItems=grn.getReceivedQuantity()-Long.valueOf(1);
		        	grn.setReceivedQuantity(ReceivedItems);
				}
	        	this.inventoryGrnRepository.save(grn);
	        	return new CommandProcessingResult(id);
	        	
	        }catch(DataIntegrityViolationException dve){
	        	handleDataIntegrityIssues(command, dve);
	        	return new CommandProcessingResult(Long.valueOf(-1));
	        }	
    }
	@SuppressWarnings("unchecked")
	@Override
	public CommandProcessingResult swapDevice(JsonCommand command) {
		String pType = null;String Type = null;String pOldSerialNo = null;String requestType = null;
		try{
			this.inventoryItemCommandFromApiJsonDeserializer.validateForSwap(command);
			
			final JsonElement element = fromJsonHelper.parse(command.json());
			final String type = command.stringValueOfParameterNamed("type");
			Type = type;
			final String serialNo = command.stringValueOfParameterNamed("oldSerialNo");
			final String oldPSerialNo = command.stringValueOfParameterNamed("oldPSerialNo");
			final String pOldPSerialNo = command.stringValueOfParameterNamed("pOldPSerialNo");
			final Long clientId=command.longValueOfParameterNamed("clientId");
			final String newSerialNo=command.stringValueOfParameterNamed("newSerialNo");
			final Long itemId =  command.longValueOfParameterNamed("itemId");
			final Long orderId =  command.longValueOfParameterNamed("orderId");
			final Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
			final String saleType = command.stringValueOfParameterNamed("saleType");
			final String remark = command.stringValueOfParameterName("remark");
			
			//unallocate device
      	  	JsonObject unallocateDevice = new JsonObject();
      	  	unallocateDevice.addProperty("serialNo",serialNo);
      	  	unallocateDevice.addProperty("clientId",clientId);
      	  	if(remark!=null){
      	  		unallocateDevice.addProperty("remark", remark);
      	  	}
      	  	JsonCommand unallocateDeviceCommand = new JsonCommand(null, unallocateDevice.toString(),unallocateDevice, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
      	  	this.deAllocateHardware(unallocateDeviceCommand);
      	  	
      	  	//allocate device
      	  	JsonObject allocateDevice = new JsonObject();
      	  	allocateDevice.addProperty("itemId",itemId);
      	  	allocateDevice.addProperty("quantity","1");
      	    allocateDevice.addProperty("saleType",saleType);
      	  	JsonArray serialNumber = new JsonArray();
      	  	JsonObject arrayObject = new JsonObject();
      	  	arrayObject.addProperty("serialNumber", newSerialNo);
      	  	arrayObject.addProperty("orderId", orderId);
      	  	arrayObject.addProperty("clientId", clientId);
      	  	arrayObject.addProperty("status", "allocated");
      	  	arrayObject.addProperty("itemMasterId", itemId);
      	  	arrayObject.addProperty("isNewHw", "Y");
      	    arrayObject.addProperty("saleType", saleType);
      	  	serialNumber.add(arrayObject);
      	  	allocateDevice.add("serialNumber", serialNumber);
      	  	JsonCommand allocateDeviceCommand = new JsonCommand(null, allocateDevice.toString(),allocateDevice, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
      	  	this.allocateHardware(allocateDeviceCommand);
			
			
			//b_item_pairing updation
      	  	ItemPairing itemPairing = this.itemPairingRepository.findOneBySerialNo(serialNo);
      	    if(itemPairing !=null){
      	    	itemPairing.setSerialNo1(newSerialNo);
      	    	pOldSerialNo = itemPairing.getSerialNo2();
      	    	pType = itemPairing.getItemType2();
      	    	this.itemPairingRepository.saveAndFlush(itemPairing);
      	    }else{
      	    	itemPairing = this.itemPairingRepository.findOneBySerialNo2(serialNo);
      	    	if(itemPairing!=null){
      	    		itemPairing.setSerialNo2(newSerialNo);
      	    		pOldSerialNo = itemPairing.getSerialNo1();
      	    		pType = itemPairing.getItemType1();
      	    		this.itemPairingRepository.saveAndFlush(itemPairing);
      	    	}
      	    }
			
      	//paired device functionality
      	  	if(command.booleanPrimitiveValueOfParameterNamed("isPairable")){
      	  		Type = ProvisioningApiConstants.REQUEST_SWAPDEVICE;
      	  		JsonArray pairableDetailsArray = fromJsonHelper.extractJsonArrayNamed("pairableItemDetails", element);
      	  		if(pairableDetailsArray.size() != 0){
      	  			for(JsonElement pairableDetails:pairableDetailsArray){
      	  				
      	  				JsonCommand pairableDetailscommand = new JsonCommand(null, pairableDetails.toString(),pairableDetails, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
      	  				this.swapDevice(pairableDetailscommand);
      	  				break;
      	  			}
      	  		}	
      	  	}
      	    
      	//generateprovisioning request
      	  	if(command.booleanPrimitiveValueOfParameterNamed("isGenerateProvisionReq")){
      	  	this.crmServices.swapDevice(command);
	      	  	List<Order> orders = new ArrayList<Order>();
  	  			for(Order order:this.orderRepository.findOrdersByClientService(clientServiceId, clientId)){
  	  				if(order.getStatus().toString().equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId().toString()) ||
  	  					order.getStatus().toString().equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.SUSPENDED).getId().toString())){
  	  				orders.add(order);
  	  				}
  	  			}
	        	  JsonObject provisioningObject = new JsonObject();
	  			switch(Type){
		  			case "SC":Type = "SWAP_VC";break;
		  			case "SWAP_DEVICE":Type = "SWAP_DEVICE";break;
		  			default:Type = "SWAP_STB";break;
	  			}
	        	provisioningObject.addProperty("requestType", Type);
	  			provisioningObject.addProperty("clientServiceId", clientServiceId);
	  			provisioningObject.addProperty("type",type);
	  			provisioningObject.addProperty("oldSerialNo", serialNo);
	  			provisioningObject.addProperty("oldPSerialNo", oldPSerialNo);
	  			if(pType != null){
		  			provisioningObject.addProperty("pType",pType);
		  			provisioningObject.addProperty("pOldSerialNo", pOldSerialNo);
		  			provisioningObject.addProperty("pOldPSerialNo", pOldPSerialNo);
      	  		}
	  			JsonCommand com = new JsonCommand(null, provisioningObject.toString(),provisioningObject, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
	  			this.provisioningWritePlatformService.createProvisioningRequest(orders, com,false);
      	  	}
      	    
      	    //updating clientservice status
      	  	ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
      	  	if("ACTIVE".equalsIgnoreCase(clientService.getStatus())){
				clientService.setStatus("PROCESSING");
				this.clientServiceRepository.saveAndFlush(clientService);
			}
      	  	
      	  	return new CommandProcessingResult(Long.valueOf(-1),clientId);
		}catch(DataIntegrityViolationException dve){
        	handleDataIntegrityIssues(command, dve);
        	return new CommandProcessingResult(Long.valueOf(-1));
        }
	}
	
	
	
	
		
}


