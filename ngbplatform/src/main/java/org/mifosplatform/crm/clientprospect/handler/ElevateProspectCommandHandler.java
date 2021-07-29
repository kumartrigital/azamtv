package org.mifosplatform.crm.clientprospect.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.ClientProspectWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@CommandType(entity = "ELEVATEPROSPECT", action = "UPDATE")
public class ElevateProspectCommandHandler implements NewCommandSourceHandler {

	private ClientProspectWritePlatformService clientProspectWritePlatformService;
	
	@Autowired
	public ElevateProspectCommandHandler(final ClientProspectWritePlatformService clientProspectWritePlatformService) {
		this.clientProspectWritePlatformService = clientProspectWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return clientProspectWritePlatformService.elevateProspect(command, command.entityId());
	}
}
