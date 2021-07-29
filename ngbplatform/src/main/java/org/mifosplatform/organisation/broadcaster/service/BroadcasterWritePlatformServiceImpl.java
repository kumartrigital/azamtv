package org.mifosplatform.organisation.broadcaster.service;

import java.util.Map;

import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.mifosplatform.organisation.broadcaster.domain.BroadcasterRepository;
import org.mifosplatform.organisation.broadcaster.exception.BroadcatserNotFoundException;
import org.mifosplatform.organisation.broadcaster.serialization.BroadcasterCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class BroadcasterWritePlatformServiceImpl implements BroadcasterWritePlatformService {
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(BroadcasterWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final BroadcasterCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final BroadcasterRepository broadcasterRepository;
	private final CrmServices crmServices;
	
	
	@Autowired
	public BroadcasterWritePlatformServiceImpl(PlatformSecurityContext context,
			BroadcasterCommandFromApiJsonDeserializer apiJsonDeserializer,
			final BroadcasterRepository broadcasterRepository,final CrmServices crmServices) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.broadcasterRepository = broadcasterRepository;
		this.crmServices = crmServices;
	}



	@Override
	public CommandProcessingResult create(JsonCommand command) {
		

		try{
		
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		final Broadcaster broadcaster = Broadcaster.formJson(command);
		this.crmServices.broadcasterconfig(command);
		this.broadcasterRepository.save(broadcaster);
		return new CommandProcessingResultBuilder().withEntityId(broadcaster.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        /*if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
            
        } */

        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
    


	@Override
	public CommandProcessingResult updateBroadcaster(JsonCommand command, Long broadcasterId){
	   try{
		   
		   this.context.authenticatedUser();
		   this.apiJsonDeserializer.validateForCreate(command.json());
		   Broadcaster broadcaster = this.retrieveCodeBy(broadcasterId);
		   final Map<String, Object> changes = broadcaster.update(command);
		   if(!changes.isEmpty()){
			   this.broadcasterRepository.save(broadcaster);
		   }
		   return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(broadcasterId) //
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		      return new CommandProcessingResult(Long.valueOf(-1));
		  }
		}
		
	
	private Broadcaster retrieveCodeBy(final Long broadcasterId) {
		final Broadcaster broadcaster = this.broadcasterRepository.findOne(broadcasterId);
		if (broadcaster == null) { throw new BroadcatserNotFoundException(broadcasterId); }
		return broadcaster;
	}	

	@Override
	public CommandProcessingResult deleteBroadcaster(Long broadcasterId) {
		try{
			this.context.authenticatedUser();
			 Broadcaster broadcaster = this.retrieveCodeBy(broadcasterId);
			if(broadcaster.getIsDeleted()=='Y'){
				throw new BroadcatserNotFoundException(broadcasterId);
			}
			broadcaster.delete();
			this.broadcasterRepository.saveAndFlush(broadcaster);
			return new CommandProcessingResultBuilder().withEntityId(broadcasterId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}

}



