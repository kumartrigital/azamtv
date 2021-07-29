package org.mifosplatform.portfolio.hardwareswapping.service;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocation;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.mifosplatform.logistics.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.portfolio.hardwareswapping.exception.WarrantyEndDateExpireException;
import org.mifosplatform.portfolio.hardwareswapping.serialization.HardwareSwappingCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderHistory;
import org.mifosplatform.portfolio.order.domain.OrderHistoryRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMapping;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMappingRepository;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 *
 */
@Service
public class HardwareSwappingWriteplatformServiceImpl implements HardwareSwappingWriteplatformService {
	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HardwareSwappingWriteplatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final HardwareAssociationWriteplatformService associationWriteplatformService;
	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final OrderRepository orderRepository;
	private final PlanRepository  planRepository;
	private final HardwareSwappingCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final OrderHistoryRepository orderHistoryRepository;
	private final ConfigurationRepository globalConfigurationRepository;
	private final OwnedHardwareJpaRepository hardwareJpaRepository;
	private final HardwareAssociationReadplatformService associationReadplatformService;
	private final ItemRepository itemRepository;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;
	private final ItemDetailsRepository itemDetailsRepository;
	private final PropertyDeviceMappingRepository propertyDeviceMappingRepository;
	  
	@Autowired
	public HardwareSwappingWriteplatformServiceImpl(final PlatformSecurityContext context,final HardwareAssociationWriteplatformService associationWriteplatformService,
			final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,final OrderRepository orderRepository,final PlanRepository planRepository,
			final ProvisioningWritePlatformService provisioningWritePlatformService,final HardwareSwappingCommandFromApiJsonDeserializer apiJsonDeserializer,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final OrderHistoryRepository orderHistoryRepository,
			final ConfigurationRepository configurationRepository,final OwnedHardwareJpaRepository hardwareJpaRepository,
			final HardwareAssociationReadplatformService associationReadplatformService,final ItemRepository itemRepository,
			final ItemDetailsRepository itemDetailsRepository,final PropertyDeviceMappingRepository propertyDeviceMappingRepository) {
 
		this.context=context;
		this.associationWriteplatformService=associationWriteplatformService;
		this.inventoryItemDetailsWritePlatformService=inventoryItemDetailsWritePlatformService;
		this.orderRepository=orderRepository;
		this.planRepository=planRepository;
		this.propertyDeviceMappingRepository = propertyDeviceMappingRepository;
		this.fromApiJsonDeserializer=apiJsonDeserializer;
		this.commandSourceWritePlatformService=commandSourceWritePlatformService;
		this.orderHistoryRepository=orderHistoryRepository;
		this.globalConfigurationRepository=configurationRepository;
		this.hardwareJpaRepository=hardwareJpaRepository;
		this.associationReadplatformService=associationReadplatformService;
		this.itemRepository=itemRepository;
		this.provisioningWritePlatformService = provisioningWritePlatformService;
		this.itemDetailsRepository = itemDetailsRepository;

	}
	
	
	
/* (non-Javadoc)
 * @see #doHardWareSwapping(java.lang.Long, org.mifosplatform.infrastructure.core.api.JsonCommand)
 */
@Transactional
@Override
public CommandProcessingResult doHardWareSwapping(final Long entityId,final JsonCommand command) {
		
	try{
		final Long userId=this.context.authenticatedUser().getId();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		final Long associationId=command.longValueOfParameterNamed("associationId");
		final String serialNo=command.stringValueOfParameterNamed("serialNo");
		final Long orderId=command.longValueOfParameterNamed("orderId");
		final String deviceAgrementType=command.stringValueOfParameterNamed("deviceAgrementType");
		final Long saleId=command.longValueOfParameterNamed("saleId");
		final String provisionNum=command.stringValueOfParameterNamed("provisionNum");
		
		//DeAssociate Hardware
		
		this.associationWriteplatformService.deAssociationHardware(associationId);
	    String requstStatus =UserActionStatusTypeEnum.DISCONNECTION.toString();
	    
	    //geting new serial number itemdetails data 
	    ItemDetails newSerailNoItemData = this.itemDetailsRepository.getInventoryItemDetailBySerialNum(provisionNum);
	    if(newSerailNoItemData == null){
	    	throw new SerialNumberNotFoundException(provisionNum);
	    }
	    LocalDate newWarrantyDate = new LocalDate(newSerailNoItemData.getWarrantyDate());
	    
        final Order order=this.orderRepository.findOne(orderId);
		final Plan plan=this.planRepository.findOne(order.getPlanId());
		
	//	Configuration configurationProperty=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
		
		
		if(deviceAgrementType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
			
			OwnedHardware ownedHardware=this.hardwareJpaRepository.findBySerialNumber(serialNo);
			ownedHardware.updateSerialNumbers(provisionNum);
			this.hardwareJpaRepository.saveAndFlush(ownedHardware);
			
			final ItemMaster itemMaster=this.itemRepository.findOne(Long.valueOf(ownedHardware.getItemType()));
	        List<HardwareAssociationData> allocationDetailsDatas=this.associationReadplatformService.retrieveClientAllocatedPlan(ownedHardware.getClientId(),itemMaster.getItemCode());
	        if(!allocationDetailsDatas.isEmpty()){
	    				this.associationWriteplatformService.createNewHardwareAssociation(ownedHardware.getClientId(),allocationDetailsDatas.get(0).getPlanId(),
	    						ownedHardware.getSerialNumber(),allocationDetailsDatas.get(0).getorderId(),"ALLOT");
	        }
	        
	   }else{
		
		//DeAllocate HardWare
		ItemDetailsAllocation inventoryItemDetailsAllocation=this.inventoryItemDetailsWritePlatformService.deAllocateHardware(serialNo, entityId,null);
		
		 JSONObject allocation = new JSONObject();
		 JSONObject allocation1 = new JSONObject();
		 JSONArray  serialNumber=new JSONArray();
		  
		 allocation.put("itemMasterId",inventoryItemDetailsAllocation.getItemMasterId());
		 allocation.put("clientId",entityId);
		 allocation.put("orderId",saleId);
		 allocation.put("serialNumber",provisionNum);
		 allocation.put("status","allocated");
		 allocation.put("isNewHw","N");
		 
		 serialNumber.put(allocation);
		 allocation1.put("quantity",1);
		 allocation1.put("itemId",inventoryItemDetailsAllocation.getItemMasterId());
		 allocation1.put("serialNumber",serialNumber);
		 
		//ReAllocate HardWare
			CommandWrapper commandWrapper = new CommandWrapperBuilder().allocateHardware().withJson(allocation1.toString()).build();
			this.commandSourceWritePlatformService.logCommandSource(commandWrapper);
		}
			//for Reassociation With New SerialNumber
		Long resouceId=Long.valueOf(0);
		
			if(!plan.getProvisionSystem().equalsIgnoreCase("None")){
			requstStatus =UserActionStatusTypeEnum.DEVICE_SWAP.toString();
			order.setStatus( OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
				CommandProcessingResult commandProcessingResult=	this.provisioningWritePlatformService.postOrderDetailsForProvisioning(order,plan.getPlanCode(),UserActionStatusTypeEnum.DEVICE_SWAP.toString(),
							Long.valueOf(0),null,serialNo,order.getId(),plan.getProvisionSystem(),null);
				resouceId=commandProcessingResult.resourceId();
			}
			
			//geting old serial number itemdetails data 
			ItemDetails oldSerailNoItemData = this.itemDetailsRepository.getInventoryItemDetailBySerialNum(serialNo);
			 LocalDate oldWarrantyDate = new LocalDate(oldSerailNoItemData.getWarrantyDate());
				oldSerailNoItemData.setWarrantyDate(newWarrantyDate);
				newSerailNoItemData.setWarrantyDate(oldWarrantyDate);
				
			 this.itemDetailsRepository.save(oldSerailNoItemData);
			 this.itemDetailsRepository.save(newSerailNoItemData);
			 
			 Configuration globalConfiguration=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_PROPERTY_MASTER);
			 
			 if(globalConfiguration.isEnabled()){
				 
				 PropertyDeviceMapping deviceMapping = this.propertyDeviceMappingRepository.findBySerailNumber(serialNo);
				 deviceMapping.setSerialNumber(provisionNum);
				 this.propertyDeviceMappingRepository.save(deviceMapping);
			 }
			 
 			this.orderRepository.save(order);
				//For Order History
				OrderHistory orderHistory=new OrderHistory(order.getId(),DateUtils.getLocalDateOfTenant(),DateUtils.getLocalDateOfTenant(),resouceId,"DEVICE SWAP",userId,null);
		
				this.orderHistoryRepository.save(orderHistory);
		return new CommandProcessingResult(entityId,order.getClientId());	
		
	   }catch(final WarrantyEndDateExpireException e){
		   Object[] obj = e.getDefaultUserMessageArgs();
		   throw new WarrantyEndDateExpireException(obj[0].toString());
	  }catch(final SerialNumberNotFoundException e){
		   	throw new SerialNumberNotFoundException(command.stringValueOfParameterNamed("provisionNum"));
	  
	  }catch(final JSONException e){
		   	e.printStackTrace();
		   	return new CommandProcessingResult(Long.valueOf(-1));
	  }catch(final DataIntegrityViolationException e){
		  handleDataIntegrityIssues(command,e);
		return new CommandProcessingResult(Long.valueOf(-1));
	  }
	
	}

  private void handleDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
	  
	  LOGGER.error(dve.getMessage(), dve);
		final Throwable realCause=dve.getCause();
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());
     }

}
