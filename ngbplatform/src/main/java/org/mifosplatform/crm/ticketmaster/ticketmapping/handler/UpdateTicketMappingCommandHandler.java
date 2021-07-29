package org.mifosplatform.crm.ticketmaster.ticketmapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.ticketmaster.ticketmapping.service.TicketMappingWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "TICKETMAPPING", action = "UPDATE")
public class UpdateTicketMappingCommandHandler implements NewCommandSourceHandler{

	private final TicketMappingWritePlatformService ticketmappingWritePlatformService;
	 
	 
	@Autowired
	public UpdateTicketMappingCommandHandler(TicketMappingWritePlatformService ticketmappingWritePlatformService) {
		this.ticketmappingWritePlatformService = ticketmappingWritePlatformService;
	}


	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.ticketmappingWritePlatformService.updateTicketmapping(command,command.entityId());
	}
	
	
	
	
}
