package org.mifosplatform.portfolio.provisioncodemapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.product.exception.ProductNotFoundException;
import org.mifosplatform.portfolio.provisioncodemapping.domain.ProvisionCodeMapping;
import org.mifosplatform.portfolio.provisioncodemapping.domain.ProvisionCodeMappingRepository;
import org.mifosplatform.portfolio.provisioncodemapping.exception.ProvisionCodeMappingNotFoundException;
import org.mifosplatform.portfolio.provisioncodemapping.serialization.ProvisionCodeMappingCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ProvisionCodeMappingWritePlatformServiceImpl implements ProvisionCodeMappingWritePlatformService{

	private final PlatformSecurityContext context;
	private final ProvisionCodeMappingCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ProvisionCodeMappingRepository provisionCodeMappingRepository;

	
	@Autowired
	public ProvisionCodeMappingWritePlatformServiceImpl(PlatformSecurityContext context,
			ProvisionCodeMappingCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			ProvisionCodeMappingRepository provisionCodeMappingRepository) {
		
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.provisionCodeMappingRepository = provisionCodeMappingRepository;
	
	}

	
	@Override
	public CommandProcessingResult createProvisionCodeMapping(JsonCommand command) {
		
		try {
			  this.context.authenticatedUser();
			  this.fromApiJsonDeserializer.validateForCreate(command.json());
			  ProvisionCodeMapping provisioncodemapping = ProvisionCodeMapping.fromJson(command);
			  provisionCodeMappingRepository.saveAndFlush(provisioncodemapping);
			   return new CommandProcessingResult(provisioncodemapping.getId());
		   }
			catch (DataIntegrityViolationException dve) {
				 handleCodeDataIntegrityIssues(command, dve);
				return  CommandProcessingResult.empty();
			}
		
	}


	private void handleCodeDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
    	final Throwable realCause = dve.getMostSpecificCause();
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
		
	
		
	}


	@Override
	public CommandProcessingResult updateProvisionCodeMapping(JsonCommand command, Long provisioncodemappingId) {

		   try{
			   
			   this.context.authenticatedUser();
			   this.fromApiJsonDeserializer.validateForCreate(command.json());
			   ProvisionCodeMapping provisionCodeMapping = this.retrieveProvisionCodeMapping(provisioncodemappingId);
			   final Map<String, Object> changes = provisionCodeMapping.update(command);
			   if(!changes.isEmpty()){
				   this.provisionCodeMappingRepository.save(provisionCodeMapping);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(provisioncodemappingId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleCodeDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
		   
	}

	private ProvisionCodeMapping retrieveProvisionCodeMapping(Long provisioncodemappingId) {
		ProvisionCodeMapping provisionCodeMapping = this.provisionCodeMappingRepository.findOne(provisioncodemappingId);
		if (provisionCodeMapping == null) { throw new ChannelNotFoundException(provisioncodemappingId); }
		return provisionCodeMapping;
	
	}


	@Override
	public CommandProcessingResult deleteProvisionCodeMapping(Long provisioncodemappingId) {

		try{
			this.context.authenticatedUser();
			ProvisionCodeMapping provisionCodeMapping = this.retrieveProvisionCodeMapping(provisioncodemappingId);
			if(provisionCodeMapping.getIsDeleted()=='Y'){
				throw new ProvisionCodeMappingNotFoundException(provisioncodemappingId);
			}
			provisionCodeMapping.delete();
			this.provisionCodeMappingRepository.saveAndFlush(provisionCodeMapping);
			return new CommandProcessingResultBuilder().withEntityId(provisioncodemappingId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	
	
	}

	
}
