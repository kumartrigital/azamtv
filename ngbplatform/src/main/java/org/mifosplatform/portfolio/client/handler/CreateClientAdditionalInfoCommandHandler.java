package org.mifosplatform.portfolio.client.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.ownedhardware.service.OwnedHardwareWritePlatformService;
import org.mifosplatform.portfolio.client.service.ClientWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateClientAdditionalInfoCommandHandler implements NewCommandSourceHandler {

	
	private final ClientWritePlatformService clientWritePlatformService;
	
	@Autowired
	public CreateClientAdditionalInfoCommandHandler(final ClientWritePlatformService clientWritePlatformService) {
		this.clientWritePlatformService = clientWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return clientWritePlatformService.createClientAdditionalInfo(command, command.entityId());
	}

}
