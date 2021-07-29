package org.mifosplatform.crm.ticketmaster.ticketmapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.ticketmaster.ticketmapping.service.TicketMappingWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "TICKETMAPPING", action = "DELETE")
public class DeleteTicketMappingCommandHandler implements NewCommandSourceHandler{

	
	private final TicketMappingWritePlatformService ticketmappingWritePlatformService;
	 
	 
	@Autowired
	public DeleteTicketMappingCommandHandler(TicketMappingWritePlatformService ticketmappingWritePlatformService) {
		this.ticketmappingWritePlatformService = ticketmappingWritePlatformService;
	}


	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return ticketmappingWritePlatformService.deleteTicketmapping(command.entityId());
	}
	
}
