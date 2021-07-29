package org.mifosplatform.logistics.grv.service;

import java.text.ParseException;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.grv.domain.GRVDetails;
import org.mifosplatform.logistics.grv.domain.GRVJpaRepository;
import org.mifosplatform.logistics.grv.serialization.GRVCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistory;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistoryJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GRVWritePlatformServiceImp implements GRVWritePlatformService{
	
	private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(GRVWritePlatformServiceImp.class);
	
	private final PlatformSecurityContext context;
	private final GRVJpaRepository grvJpaRepository;
	private final GRVCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final GRVReadPlatformService grvReadPlatformService;
	private final ItemDetailsRepository inventoryItemDetailsRepository;
	private final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository;
	
	
	@Autowired
	public GRVWritePlatformServiceImp(final GRVJpaRepository grvJpaRepository,final PlatformSecurityContext context,
			final GRVCommandFromApiJsonDeserializer apiJsonDeserializer,final GRVReadPlatformService grvReadPlatformService,
			final ItemDetailsRepository inventoryItemDetailsRepository,
			final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.grvJpaRepository = grvJpaRepository;
		this.grvReadPlatformService = grvReadPlatformService;
		this.inventoryItemDetailsRepository = inventoryItemDetailsRepository;
		this.inventoryTransactionHistoryJpaRepository = inventoryTransactionHistoryJpaRepository;
		
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createGRV(JsonCommand command) {
		try {
			this.apiJsonDeserializer.validateForCreate(command.json());
			final GRVDetails grvDetails = GRVDetails.formJson(command);
			this.grvJpaRepository.save(grvDetails);
			return new CommandProcessingResultBuilder().withEntityId(grvDetails.getId()).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();
		
		}catch (ParseException e) {
			throw new PlatformDataIntegrityException("invalid.date.format", "invalid.date.format", "purchaseDate");
		}
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult moveGRV(JsonCommand command) {
		
		/*try {
			context.authenticatedUser();
			apiJsonDeserializer.validateForMove(command.json());
			final Long grvId = command.longValueOfParameterNamed("grvId");
			final String serialNumber=command.stringValueOfParameterNamed("serialNumber");
			final GRVDetails grvDetails = this.grvJpaRepository.findOne(grvId);
			if(grvDetails == null){
				throw new PlatformDataIntegrityException("invalid.grv", "invalid grv");
			}
			final List<String> serialNumbers = this.grvReadPlatformService.retriveSerialNumbersForItems(grvId);
			if(!serialNumbers.contains(serialNumber)){
				throw new PlatformDataIntegrityException("invalid.serialnumber.allocation", "invalid.serialnumber.allocation", "serialNumber","");
			}
			
			ItemDetails details = inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(serialNumber);
			if(details == null){
				throw new ItemNotFoundException(serialNumber);
			}
			if(details.getOfficeId().equals(grvDetails.getToOffice())){
				throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation", "invalid.move.operation");
			}
			
			details.setOfficeId(grvDetails.getToOffice());
			if(grvDetails.getReceivedQuantity() < grvDetails.getOrderdQuantity()){
				grvDetails.setReceivedQuantity(grvDetails.getReceivedQuantity()+1);
				grvDetails.setStatus("Pending");
			} else if(grvDetails.getReceivedQuantity().equals(grvDetails.getOrderdQuantity())){
				throw new PlatformDataIntegrityException("received.quantity.is.full", "received.quantity.is.full", "received quantity is full");
			}
			
			InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(grvDetails.getRequestedDate(), grvId,"GRV", serialNumber,grvDetails.getItemMasterId(),
					grvDetails.getFromOffice(), grvDetails.getToOffice());
			
			details.setOfficeId(grvDetails.getToOffice());
			inventoryItemDetailsRepository.save(details);
			inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			
			if(grvDetails.getOrderdQuantity().equals(grvDetails.getReceivedQuantity())){
				grvDetails.setStatus("Completed");
			}
			grvJpaRepository.save(grvDetails);
			return new CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();
		}
	
	}*/
		
		
		try {
			context.authenticatedUser();
			apiJsonDeserializer.validateForMove(command.json());
			final Long grvId = command.longValueOfParameterNamed("grvId");
			final String serialNumber=command.stringValueOfParameterNamed("serialNumber");
			final String serialNumbers = this.grvReadPlatformService.retriveSerialNumbersFromGrv(serialNumber, grvId);
			if(serialNumbers == null){
				throw new PlatformDataIntegrityException("invalid.serialnumber.allocation", "invalid.serialnumber.allocation", "serialNumber","");
			}
			final GRVDetails grvDetails = this.grvJpaRepository.findOne(grvId);
			if(grvDetails == null){
				throw new PlatformDataIntegrityException("invalid.grv", "invalid grv");
			}
			
			ItemDetails details = inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(serialNumber);
			if(details == null){
				throw new ItemNotFoundException(serialNumber);
			}
			if(details.getOfficeId().equals(grvDetails.getToOffice())){
				throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation", "invalid.move.operation");
			}
			
			details.setOfficeId(grvDetails.getToOffice());
			if(grvDetails.getReceivedQuantity() < grvDetails.getOrderdQuantity()){
				grvDetails.setReceivedQuantity(grvDetails.getReceivedQuantity()+1);
				grvDetails.setStatus("Pending");
			} else if(grvDetails.getReceivedQuantity().equals(grvDetails.getOrderdQuantity())){
				throw new PlatformDataIntegrityException("received.quantity.is.full", "received.quantity.is.full", "received quantity is full");
			}
			
			InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(grvDetails.getRequestedDate(), grvId,"GRV", serialNumber,grvDetails.getItemMasterId(),
					grvDetails.getFromOffice(), grvDetails.getToOffice());
			
			details.setOfficeId(grvDetails.getToOffice());
			inventoryItemDetailsRepository.save(details);
			inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			
			if(grvDetails.getOrderdQuantity().equals(grvDetails.getReceivedQuantity())){
				grvDetails.setStatus("Completed");
			}
			grvJpaRepository.save(grvDetails);
			return new CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();
		}
	
	}
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        Throwable realCause = dve.getMostSpecificCause();
       if (realCause.getMessage().contains("serial_no_constraint")){
       	throw new PlatformDataIntegrityException("validation.error.msg.inventory.mrn.duplicate.entry", "validation.error.msg.inventory.mrn.duplicate.entry", "validation.error.msg.inventory.mrn.duplicate.entry","");
       	
       }
       LOGGER.error(dve.getMessage(), dve);
	}
}
	
	


