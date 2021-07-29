package org.mifosplatform.crm.ticketmaster.ticketmapping.service;

import java.util.Set;

import org.mifosplatform.crm.ticketmaster.ticketmapping.domain.TicketMappingRepository;
import org.mifosplatform.crm.ticketmaster.ticketmapping.domain.TicketTeamMapping;
import org.mifosplatform.crm.ticketmaster.ticketmapping.exception.TicketMappingNotFoundException;
import org.mifosplatform.crm.ticketmaster.ticketmapping.serialization.TicketMappingCommandFromApiJsonDeserializer;
import org.mifosplatform.crm.ticketmaster.ticketteam.domain.TicketTeam;
import org.mifosplatform.crm.ticketmaster.ticketteam.domain.TicketTeamRepository;
import org.mifosplatform.crm.ticketmaster.ticketteam.exception.TicketTeamNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class TicketMappingWritePlatformServiceImpl implements TicketMappingWritePlatformService{

	private final static Logger logger = (Logger) LoggerFactory.getLogger(TicketMappingWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final TicketMappingCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final TicketMappingRepository ticketmappingRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final TicketTeamRepository ticketTeamRepository;
	
	
	
	@Autowired
	public TicketMappingWritePlatformServiceImpl(PlatformSecurityContext context,
			TicketMappingCommandFromApiJsonDeserializer apiJsonDeserializer,
			final TicketMappingRepository ticketmappingRepository, final FromJsonHelper fromJsonHelper,
			final TicketTeamRepository ticketTeamRepository) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.ticketmappingRepository = ticketmappingRepository;
		this.fromApiJsonHelper=fromJsonHelper;
		this.ticketTeamRepository = ticketTeamRepository;
	}

	
	@Override
	public CommandProcessingResult createTicketMapping(JsonCommand command) {

		try{
		
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		TicketTeamMapping ticketteammapping = TicketTeamMapping.formJson(command);
   	    Long teamId = command.longValueOfParameterNamed("teamId");
   	    final String[] users = command.arrayValueOfParameterNamed("userId");
		for (final String user : users) {
			Long userId  = Long.valueOf(user);
			this.ticketmappingRepository.saveAndFlush( new TicketTeamMapping(teamId,userId));				 
       }
		return new CommandProcessingResultBuilder().withEntityId(ticketteammapping.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
	
	


	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();

        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }


	@Override
	public CommandProcessingResult updateTicketmapping(JsonCommand command, Long ticketmappingId) {
		
		try{
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   Set<TicketTeamMapping> ticketteammapping = this.retrieveCodeBy(ticketmappingId);
			   Set<TicketTeamMapping> ticketteammappings = this.userDetail(ticketteammapping,command.arrayValueOfParameterNamed("userId"),ticketmappingId);
			   this.ticketmappingRepository.save(ticketteammappings);
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(ticketmappingId) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
		
	     }
	
	     private Set<TicketTeamMapping> userDetail(Set<TicketTeamMapping> ticketteammappings,
			String[] userArray, Long ticketmappingId) {
	 		for(TicketTeamMapping ticketteammapping:ticketteammappings){
	 			boolean deletable = true;
	 			for (final String userId : userArray) {
	 				if(ticketteammapping.getUserId().toString().equalsIgnoreCase(userId)){
	 					deletable = false;
	 				}
	 			}
	 			if(deletable){
	 				ticketteammapping.delete();
	 				/*usercataloges.add(usercataloge);*/
	 			}
	 			
	 		}
	 		
	 		for(final String userId : userArray) {
	 			boolean isNew= true;
	 			for(TicketTeamMapping ticketteammapping:ticketteammappings){
	 				if(ticketteammapping.getUserId().toString().equalsIgnoreCase(userId)){
	 					isNew = false;break;
	 				}
	 			}
	 			if(isNew){
	 				ticketteammappings.add(new TicketTeamMapping(ticketmappingId,Long.valueOf(userId)));
	 			}
	 			
	         }
	 		
	 		return ticketteammappings;
	 	}


		private Set<TicketTeamMapping> retrieveCodeBy(final Long ticketmappingId) {
		 Set<TicketTeamMapping> ticketteammapping = this.ticketmappingRepository.findTicketMappingId(ticketmappingId);
		 if (ticketteammapping == null) { throw new TicketMappingNotFoundException(ticketmappingId); }
			return ticketteammapping;
		 }

  
	     @Override
	 	public CommandProcessingResult deleteTicketmapping(Long ticketmappingId) {
	 		
	 		try{
	 			this.context.authenticatedUser();
	 			TicketTeamMapping ticketteammapping = this.retrieve(ticketmappingId);
	 			if(ticketteammapping.getIsDeleted()=='Y'){
	 				throw new TicketTeamNotFoundException(ticketmappingId);
	 			}
	 			ticketteammapping.delete();
	 			this.ticketmappingRepository.saveAndFlush(ticketteammapping);
	 			return new CommandProcessingResultBuilder().withEntityId(ticketmappingId).build();
	 			
	 		}catch(DataIntegrityViolationException dve){
	 			handleDataIntegrityIssues(null, dve);
	 			return new CommandProcessingResult(Long.valueOf(-1));
	 		}
	 	}


		private TicketTeamMapping retrieve(Long ticketmappingId) {
			TicketTeamMapping ticketteammapping = this.ticketmappingRepository.findOne(ticketmappingId);
			if (ticketteammapping == null) { throw new TicketMappingNotFoundException(ticketmappingId); }
			return ticketteammapping;
		}


	    

	
}
