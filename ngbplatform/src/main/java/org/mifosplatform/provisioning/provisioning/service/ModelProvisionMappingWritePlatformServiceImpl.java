package org.mifosplatform.provisioning.provisioning.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.provisioning.provisioning.domain.ModelProvisionMapping;
import org.mifosplatform.provisioning.provisioning.domain.ModelProvisionMappingRepository;
import org.mifosplatform.provisioning.provisioning.serialization.ModelProvisionMappingCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ModelProvisionMappingWritePlatformServiceImpl implements ModelProvisionMappingWritePlatformService{
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(ModelProvisionMappingWritePlatformServiceImpl.class);
	
	private final PlatformSecurityContext context;
	private final ModelProvisionMappingCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ModelProvisionMappingRepository modelProvisionMappingRepository; 
	
	
	@Autowired
	public ModelProvisionMappingWritePlatformServiceImpl(final PlatformSecurityContext context,
			final ModelProvisionMappingCommandFromApiJsonDeserializer apiJsonDeserializer,
			final ModelProvisionMappingRepository modelProvisionMappingRepository) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.modelProvisionMappingRepository = modelProvisionMappingRepository;
	}



	@Override
	public CommandProcessingResult createModelProvisionMapping(JsonCommand command) {
		try{
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			ModelProvisionMapping modelProvisionMapping = ModelProvisionMapping.formJson(command);
			this.modelProvisionMappingRepository.save(modelProvisionMapping);
			return new CommandProcessingResultBuilder().withEntityId(modelProvisionMapping.getId()).build();
			
		}catch (DataIntegrityViolationException dve) {
			        handleDataIntegrityIssues(command, dve);
			        return  CommandProcessingResult.empty();
		}
	}
	
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("external_id")) {
            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
        }

        throw new PlatformDataIntegrityException("error.msg.client.ModelProvisionMapping.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }



	@Override
	public CommandProcessingResult updateModelProvisionMapping(JsonCommand command, Long modelProvisionMappingId) {
		 try{
			 this.context.authenticatedUser();
			 this.apiJsonDeserializer.validateForCreate(command.json());
			 ModelProvisionMapping modelProvisionMapping = this.retriveModelProvisionMapping(modelProvisionMappingId);
			 final Map<String, Object> changes = modelProvisionMapping.update(command);
			 if(!changes.isEmpty()){
				   this.modelProvisionMappingRepository.save(modelProvisionMapping);
			 }
			   
			 return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(modelProvisionMappingId) //
		       .with(changes) //
		       .build();
		 }catch (DataIntegrityViolationException dve) {
			 	handleDataIntegrityIssues(command, dve);
			    return new CommandProcessingResult(Long.valueOf(-1));
		 }
	}



	@Override
	public CommandProcessingResult deleteModelProvisionMapping(Long modelProvisionMappingId) {
		try{
			ModelProvisionMapping modelProvisionMapping = this.retriveModelProvisionMapping(modelProvisionMappingId);
			if(modelProvisionMapping.getDeleted() == 'Y'){
				throw new PlatformDataIntegrityException("modelProvisionMapping.not.found.exception","ModelProvisionMapping does not exist with "+modelProvisionMappingId);
			}
			modelProvisionMapping.delete();
			this.modelProvisionMappingRepository.saveAndFlush(modelProvisionMapping);
			return new CommandProcessingResultBuilder().withEntityId(modelProvisionMappingId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}
	
	private ModelProvisionMapping retriveModelProvisionMapping(Long id){
		ModelProvisionMapping modelProvisionMapping= this.modelProvisionMappingRepository.findOne(id);
		if(modelProvisionMapping == null){
			throw new PlatformDataIntegrityException("modelProvisionMapping.not.found.exception","ModelProvisionMapping does not exist with "+id);
		}
		return modelProvisionMapping;
	}
	
}
