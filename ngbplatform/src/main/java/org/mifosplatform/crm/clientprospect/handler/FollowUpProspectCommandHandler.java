package org.mifosplatform.crm.clientprospect.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.ClientProspectWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowUpProspectCommandHandler implements NewCommandSourceHandler {

	private ClientProspectWritePlatformService clientProspectWritePlatformService;

	@Autowired
	public FollowUpProspectCommandHandler(final ClientProspectWritePlatformService clientProspectWritePlatformService) {
		this.clientProspectWritePlatformService = clientProspectWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return clientProspectWritePlatformService.followUpProspect(command, command.entityId());
	}

}
