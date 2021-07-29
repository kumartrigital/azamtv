package org.mifosplatform.crm.clientprospect.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.ClientProspectWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProspectCommandHandler implements NewCommandSourceHandler {

	private final ClientProspectWritePlatformService clientProspectWritePlatformService;

	@Autowired
	public UpdateProspectCommandHandler(final ClientProspectWritePlatformService clientProspectWritePlatformService) {
		
		this.clientProspectWritePlatformService = clientProspectWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		
		return this.clientProspectWritePlatformService.updateProspect(command);
	}
}
