package org.mifosplatform.organisation.mapping.service;

import java.util.Map;
import java.util.Set;

import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.crm.ticketmaster.ticketmapping.domain.TicketTeamMapping;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.organisation.mapping.domain.ChannelMapping;
import org.mifosplatform.organisation.mapping.domain.ChannelMappingRepository;
import org.mifosplatform.organisation.mapping.exception.ChannelMappingNotFoundException;
import org.mifosplatform.organisation.mapping.serialization.ChannelMappingCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.usercataloge.domain.UserCataloge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChannelMappingWritePlatformServiceImpl implements ChannelMappingWritePlatformService {
    
	private final PlatformSecurityContext context;
	private final ChannelMappingCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ChannelMappingRepository channelmappingRepository;
	private final CrmServices crmServices;
	
	@Autowired
	public ChannelMappingWritePlatformServiceImpl(PlatformSecurityContext context,
			ChannelMappingCommandFromApiJsonDeserializer apiJsonDeserializer, ChannelMappingRepository channelmappingRepository,final CrmServices crmServices) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.channelmappingRepository = channelmappingRepository;
		this.crmServices = crmServices;
	}


	@Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
		
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		ChannelMapping channelmapping = ChannelMapping.formJson(command);
		Long productId = command.longValueOfParameterNamed("productId");
   	    final String[] channels = command.arrayValueOfParameterNamed("channelDetail");
		for (final String channel : channels) {
			Long channelId  = Long.valueOf(channel);
			this.channelmappingRepository.saveAndFlush( new ChannelMapping(productId,channelId));				 
       }
		//this.channelmappingRepository.save(channelmapping);
		return new CommandProcessingResultBuilder().withEntityId(channelmapping.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	
	}


	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
    	final Throwable realCause = dve.getMostSpecificCause();
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
		
	}

	@Transactional
	@Override
	public CommandProcessingResult updateChannelMapping(JsonCommand command, Long productId) {  
		
		try{
		   
		   this.context.authenticatedUser();
		   this.apiJsonDeserializer.validateForCreate(command.json());
		   Set<ChannelMapping> channelmapping = this.retrieveCodeBy(productId);
		   Set<ChannelMapping> channelmappings = this.channelDetail(channelmapping,command.arrayValueOfParameterNamed("channelDetail"),productId);
		   this.channelmappingRepository.save(channelmappings);
		   /*final Map<String, Object> changes = channelmapping.update(command);
		   if(!changes.isEmpty()){
			   this.channelmappingRepository.save(channelmapping);
		   }*/
		   return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(productId) //
	       //.with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		      return new CommandProcessingResult(Long.valueOf(-1));
		 }
	}
	
	private Set<ChannelMapping> channelDetail(Set<ChannelMapping> channelmappings, String[] channelArray, Long productId) {
	 		for(ChannelMapping channelmapping:channelmappings){
	 			boolean deletable = true;
	 			for (final String channelId : channelArray) {
	 				if(channelmapping.getChannelId().toString().equalsIgnoreCase(channelId)){
	 					deletable = false;
	 				}
	 			}
	 			if(deletable){
	 				channelmapping.delete();
	 				
	 			}
	 			
	 		}
	 		
	 		for(final String channelId : channelArray) {
	 			boolean isNew= true;
	 			for(ChannelMapping channelmapping:channelmappings){
	 				if(channelmapping.getChannelId().toString().equalsIgnoreCase(channelId)){
	 					isNew = false;break;
	 				}
	 			}
	 			if(isNew){
	 				channelmappings.add(new ChannelMapping(productId,Long.valueOf(channelId)));
	 			}
	 			
	         }
	 		
	 		return channelmappings;
	 	}
	
	
	private Set<ChannelMapping> retrieveCodeBy(final Long productId) {
		Set<ChannelMapping> channelmapping = this.channelmappingRepository.findByChannelIdValue(productId);
		if (channelmapping == null) { throw new ChannelNotFoundException(productId); }
		return channelmapping;
	}
	

	@Override
	public CommandProcessingResult deleteChannelMapping(Long productId) {
		try{
			this.context.authenticatedUser();
			ChannelMapping channelmapping = this.retrieveChannelMapping(productId);
			if(channelmapping.getIsDeleted()=='Y'){
				throw new ChannelMappingNotFoundException(productId);
			}
			channelmapping.delete();
			this.channelmappingRepository.saveAndFlush(channelmapping);
			return new CommandProcessingResultBuilder().withEntityId(productId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	private ChannelMapping retrieveChannelMapping(final Long productId) {
		ChannelMapping channelmapping = this.channelmappingRepository.findOne(productId);
		if (channelmapping == null) { throw new ChannelNotFoundException(productId); }
		return channelmapping;
	}
	
}
