package org.mifosplatform.portfolio.clientservice.service;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@CommandType(entity = "CLIENTSERVICE", action = "TERMINATE")
public class TerminateClientServiceCommandHandler implements NewCommandSourceHandler{
	
	private final ClientServiceWriteplatformService clientServiceWritePlatformService;

    @Autowired
    public TerminateClientServiceCommandHandler(final ClientServiceWriteplatformService clientServiceWritePlatformService) {
        this.clientServiceWritePlatformService = clientServiceWritePlatformService;
    }

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.clientServiceWritePlatformService.terminateClientService(command.entityId(),command);
	}

}
