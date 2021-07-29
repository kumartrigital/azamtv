package org.mifosplatform.provisioning.networkelement.service;

import java.util.Map;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.portfolio.note.serialization.NoteCommandFromApiJsonDeserializer;
import org.mifosplatform.provisioning.networkelement.api.NetworkElementApiResource;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElement;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElementRepository;
import org.mifosplatform.provisioning.networkelement.exception.NetworkElementNotFoundException;
import org.mifosplatform.provisioning.networkelement.serialization.NetworkElementCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class NetworkElementWritePlatformServiceImpl implements NetworkElementWritePlatformService{
	
	private final PlatformSecurityContext context;
	private final NetworkElementCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final NetworkElementRepository networkelementRepository;
	
	@Autowired
	public NetworkElementWritePlatformServiceImpl(PlatformSecurityContext context,
			NetworkElementCommandFromApiJsonDeserializer apiJsonDeserializer,
			final NetworkElementRepository networkelementRepository) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.networkelementRepository = networkelementRepository;
	}
	
	

	@Override
	public CommandProcessingResult create(JsonCommand command) {
		
		try{
		
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		NetworkElement networkelement = NetworkElement.formJson(command);
		networkelementRepository.saveAndFlush(networkelement);
		return new CommandProcessingResultBuilder().withEntityId(networkelement.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
	
	@Override
	public CommandProcessingResult updateNetworkElement(JsonCommand command, Long networkelementId) {

		   try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   NetworkElement networkelement = this.networkelementRepository.findOne(networkelementId);
			   final Map<String, Object> changes = networkelement.update(command);
			   if(!changes.isEmpty()){
				   this.networkelementRepository.save(networkelement);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(networkelementId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
			    return new CommandProcessingResult(Long.valueOf(-1));
			  }
			
	}
	
	

	@Override
	public CommandProcessingResult deleteNetworkElement(Long networkelementId) {

		try{
			this.context.authenticatedUser();
			NetworkElement networkelement = this.retrieveNetworkElement(networkelementId);
			if(networkelement.getIsDeleted()=='Y'){
				throw new NetworkElementNotFoundException(networkelementId);
			}
			networkelement.delete();
			this.networkelementRepository.saveAndFlush(networkelement);
			return new CommandProcessingResultBuilder().withEntityId(networkelementId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	
	}
	private NetworkElement retrieveNetworkElement(final Long networkelementId) {
		NetworkElement networkelement = this.networkelementRepository.findOne(networkelementId);
		if (networkelement == null) { throw new NetworkElementNotFoundException(networkelementId); }
		return networkelement;
	}
	
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

	
	
}
