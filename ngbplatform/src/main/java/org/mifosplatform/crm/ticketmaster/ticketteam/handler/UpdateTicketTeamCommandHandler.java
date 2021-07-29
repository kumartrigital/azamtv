package org.mifosplatform.crm.ticketmaster.ticketteam.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.ticketmaster.ticketteam.service.TicketTeamWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "TICKETTEAM", action = "UPDATE")

public class UpdateTicketTeamCommandHandler implements NewCommandSourceHandler {
	

	 private final TicketTeamWritePlatformService ticketteamWritePlatformService;
	 
		
	 
		@Autowired
		public UpdateTicketTeamCommandHandler(TicketTeamWritePlatformService ticketteamWritePlatformService) {
			this.ticketteamWritePlatformService = ticketteamWritePlatformService;
		}

		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
			
			return this.ticketteamWritePlatformService.updateTicketteam(command,command.entityId());
		}
		
		
		
	

}
