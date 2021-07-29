package org.mifosplatform.crm.ticketmaster.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "OFFICETICKET", action = "CREATE")
public class CreateTicketOfficeCommandHandler implements
		NewCommandSourceHandler {

	private final TicketMasterWritePlatformService ticketMasterWritePlatformService;
	@Autowired
	CreateTicketOfficeCommandHandler(final TicketMasterWritePlatformService ticketMasterWritePlatformService){
		this.ticketMasterWritePlatformService=ticketMasterWritePlatformService;
	}
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.ticketMasterWritePlatformService.createOfficeTicket(command);
	}

}
