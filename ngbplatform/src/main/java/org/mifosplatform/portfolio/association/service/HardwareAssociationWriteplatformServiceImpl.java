package org.mifosplatform.portfolio.association.service;

import java.util.Map;

import net.sf.json.JSONObject;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.portfolio.allocation.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.association.exception.HardwareDetailsNotFoundException;
import org.mifosplatform.portfolio.hardwareswapping.exception.WarrantyEndDateExpireException;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.workflow.eventvalidation.service.EventValidationReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HardwareAssociationWriteplatformServiceImpl implements HardwareAssociationWriteplatformService
{

	private final PlatformSecurityContext context;
	private final OrderRepository orderRepository;
	private final HardwareAssociationRepository associationRepository;
	private final EventValidationReadPlatformService eventValidationReadPlatformService;
	private final HardwareAssociationCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ItemDetailsRepository itemDetailsRepository;
	
    @Autowired
	public HardwareAssociationWriteplatformServiceImpl(final PlatformSecurityContext context,
			final HardwareAssociationCommandFromApiJsonDeserializer fromApiJsonDeserializer,final HardwareAssociationRepository associationRepository,
			final OrderRepository orderRepository,final EventValidationReadPlatformService eventValidationReadPlatformService ,
			final ItemDetailsRepository itemDetailsRepository){
		
	    this.context=context;
		this.associationRepository=associationRepository;
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.orderRepository=orderRepository;
		this.eventValidationReadPlatformService=eventValidationReadPlatformService;
		this.itemDetailsRepository=itemDetailsRepository;
	}
	
	@Override
	public void createNewHardwareAssociation(Long clientId, Long planId,String serialNo,Long orderId,String allocationType) 
	{
	        try{
	        	
	        //	this.context.authenticatedUser();
	        	HardwareAssociation hardwareAssociation=new HardwareAssociation(clientId,planId,serialNo,orderId,allocationType);
	        	this.associationRepository.saveAndFlush(hardwareAssociation);

	        }catch(DataIntegrityViolationException exception){
	        	exception.printStackTrace();
	        }
		
	}

	@Override
	public CommandProcessingResult createAssociation(JsonCommand command) {
		try {
			context.authenticatedUser();
			final Long userId=getUserId();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			Long orderId = command.longValueOfParameterNamed("orderId");
			Order order=this.orderRepository.findOne(orderId);
			String provisionNum = command.stringValueOfParameterNamed("provisionNum");
			String allocationType = command.stringValueOfParameterNamed("allocationType");
			HardwareAssociation hardwareAssociation = new HardwareAssociation(command.entityId(), order.getPlanId(), provisionNum, orderId,allocationType);
			//Check for Custome_Validation
			this.eventValidationReadPlatformService.checkForCustomValidations(hardwareAssociation.getClientId(),"Pairing", command.json(),userId);
			this.associationRepository.saveAndFlush(hardwareAssociation);
			return new CommandProcessingResultBuilder().withEntityId(
					hardwareAssociation.getId()).withClientId(command.entityId()).build();
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		
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

	@Override
	public CommandProcessingResult updateAssociation(JsonCommand command) {
		
		// TODO Auto-generated method stub
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			HardwareAssociation hardwareAssociation = this.associationRepository.findOne(command.entityId());
			final Map<String, Object> changes = hardwareAssociation.updateAssociationDetails(command);
			if (!changes.isEmpty()) {
				this.associationRepository.save(hardwareAssociation);
			}
			return new CommandProcessingResult(hardwareAssociation.getId(),hardwareAssociation.getClientId());
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	@Transactional
	@Override
	public CommandProcessingResult deAssociationHardware(final Long associationId) {
		
		try {
			
//			AssociationData associationData=this.associationReadplatformService.retrieveSingleDetails(orderId);
			
		      HardwareAssociation association=this.associationRepository.findOne(associationId);
		      if(association == null){
					throw new HardwareDetailsNotFoundException(associationId);
				}
		      ItemDetails itemDetails = this.itemDetailsRepository.getInventoryItemDetailBySerialNum(association.getSerialNo());
			    if(itemDetails.getWarrantyDate() != null){
				    if(itemDetails.getWarrantyDate().before(DateUtils.getDateOfTenant())){
				    	throw new WarrantyEndDateExpireException(association.getSerialNo());
				    }
			    }
		      
		      JSONObject jsonObject=new JSONObject();
		      jsonObject.put("clientId", association.getClientId());
		      jsonObject.put("planId", association.getPlanId());
		      jsonObject.put("serialNo", association.getSerialNo());
		      jsonObject.put("orderId", association.getOrderId());
		      
		       
		        //Check for Custome_Validation
             this.eventValidationReadPlatformService.checkForCustomValidations(association.getClientId(),"UnPairing", jsonObject.toString(),getUserId());
             
    		   association.delete();
    		   this.associationRepository.save(association);
    		   return new CommandProcessingResult(association.getId(),association.getClientId());
    		   
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	
	}

}
