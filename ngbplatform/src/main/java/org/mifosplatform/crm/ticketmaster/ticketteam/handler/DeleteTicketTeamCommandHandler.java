package org.mifosplatform.crm.ticketmaster.ticketteam.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.ticketmaster.ticketteam.service.TicketTeamWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "TICKETTEAM", action = "DELETE")
public class DeleteTicketTeamCommandHandler implements NewCommandSourceHandler{

	 private final TicketTeamWritePlatformService ticketteamWritePlatformService;
	 
	 
		@Autowired
		public DeleteTicketTeamCommandHandler(TicketTeamWritePlatformService ticketteamWritePlatformService) {
			this.ticketteamWritePlatformService = ticketteamWritePlatformService;
		}

		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
			return ticketteamWritePlatformService.deleteTicketteam(command.entityId());
		}
		
	
}
