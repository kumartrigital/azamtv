package org.mifosplatform.organisation.channel.service;

import java.util.Map;

import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.domain.ChannelRepository;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.organisation.channel.serialization.ChannelCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ChannelWritePlatformServiceImpl implements ChannelWritePlatformService{
	
	private final PlatformSecurityContext context;
	private final ChannelCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ChannelRepository channelRepository;
	private final CrmServices crmServices;
	@Autowired
	public ChannelWritePlatformServiceImpl(PlatformSecurityContext context,
			ChannelCommandFromApiJsonDeserializer apiJsonDeserializer,
			final ChannelRepository channelRepository,final CrmServices crmServices) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.channelRepository = channelRepository;
		this.crmServices = crmServices;
	}


	@Override
	public CommandProcessingResult create(JsonCommand command) {
		

		try{
		
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		Channel channel = Channel.formJson(command);
		channelRepository.saveAndFlush(channel);
		this.crmServices.channelconfig(command);
		return new CommandProcessingResultBuilder().withEntityId(channel.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
	
	/*private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }*/
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        if(realCause.getMessage().contains("channel_sequence")){
            final String channelSequence = command.stringValueOfParameterNamed("channelSequence");
            throw new PlatformDataIntegrityException("error.msg.duplicate.channelSequence", "Client with channelSequence `" + channelSequence
                    + "` already exists", "channelSequence", channelSequence);
        }
        
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }


	@Override
	public CommandProcessingResult updateChannel(JsonCommand command, Long channelId) {

		   try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   Channel channel = this.retrieveChannel(channelId);
			   final Map<String, Object> changes = channel.update(command);
			   if(!changes.isEmpty()){
				   this.channelRepository.saveAndFlush(channel);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(channelId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
			
	}
	


	@Override
	public CommandProcessingResult deleteChannel(Long channelId) {

		try{
			this.context.authenticatedUser();
			Channel channel = this.retrieveChannel(channelId);
			if(channel.getIsDeleted()=='Y'){
				throw new ChannelNotFoundException(channelId);
			}
			channel.delete();
			this.channelRepository.saveAndFlush(channel);
			return new CommandProcessingResultBuilder().withEntityId(channelId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	
	}

	private Channel retrieveChannel(final Long channelId) {
		Channel channel = this.channelRepository.findOne(channelId);
		if (channel == null) { throw new ChannelNotFoundException(channelId); }
		return channel;
	}

	
}
