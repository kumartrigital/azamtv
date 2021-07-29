package org.mifosplatform.crm.ticketmaster.ticketteam.service;

import java.util.Map;

import org.mifosplatform.crm.ticketmaster.ticketteam.domain.TicketTeam;
import org.mifosplatform.crm.ticketmaster.ticketteam.domain.TicketTeamRepository;
import org.mifosplatform.crm.ticketmaster.ticketteam.exception.TicketTeamNotFoundException;
import org.mifosplatform.crm.ticketmaster.ticketteam.serialization.TicketTeamCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class TicketTeamWritePlatformServiceImpl implements  TicketTeamWritePlatformService{


	private final static Logger logger = (Logger) LoggerFactory.getLogger(TicketTeamWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final TicketTeamCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final TicketTeamRepository ticketteamRepository;
	
	
	@Autowired
	public TicketTeamWritePlatformServiceImpl(PlatformSecurityContext context,
			TicketTeamCommandFromApiJsonDeserializer apiJsonDeserializer,
			final TicketTeamRepository ticketteamRepository) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.ticketteamRepository = ticketteamRepository;
	}

	

	@Override
	public CommandProcessingResult createTicketTeam(JsonCommand command) {

		try{
		
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		final TicketTeam ticketteam = TicketTeam.formJson(command);
		this.ticketteamRepository.save(ticketteam);
		return new CommandProcessingResultBuilder().withEntityId(ticketteam.getId()).build();
		
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
	public CommandProcessingResult updateTicketteam(JsonCommand command,Long ticketteamId) {
	
		try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   TicketTeam ticketteam = this.retrieveCodeBy(ticketteamId);
			   final Map<String, Object> changes = ticketteam.update(command);
			   if(!changes.isEmpty()){
				   this.ticketteamRepository.save(ticketteam);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(ticketteamId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
		
	     }
    
	   private TicketTeam retrieveCodeBy(final Long ticketteamId) {
		final TicketTeam ticketteam = this.ticketteamRepository.findOne(ticketteamId);
		if (ticketteam == null) { throw new TicketTeamNotFoundException(ticketteamId); }
		return ticketteam;
	}



	@Override
	public CommandProcessingResult deleteTicketteam(Long ticketteamId) {
		
		try{
			this.context.authenticatedUser();
			TicketTeam ticketteam = this.retrieveCodeBy(ticketteamId);
			if(ticketteam.getIsDeleted()=='Y'){
				throw new TicketTeamNotFoundException(ticketteamId);
			}
			ticketteam.delete();
			this.ticketteamRepository.saveAndFlush(ticketteam);
			return new CommandProcessingResultBuilder().withEntityId(ticketteamId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}	
	
	
	
	
	
	
	
	
	
}
