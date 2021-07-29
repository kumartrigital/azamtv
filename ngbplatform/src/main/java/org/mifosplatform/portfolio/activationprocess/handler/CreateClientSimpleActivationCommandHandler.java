package org.mifosplatform.portfolio.activationprocess.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.activationprocess.service.ActivationProcessWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CLIENTSIMPLEACTIVATION", action = "CREATE")
public class CreateClientSimpleActivationCommandHandler  implements NewCommandSourceHandler{

	private final ActivationProcessWritePlatformService activationProcessWritePlatformService;

	@Autowired
	public CreateClientSimpleActivationCommandHandler(ActivationProcessWritePlatformService activationProcessWritePlatformService) {
		this.activationProcessWritePlatformService = activationProcessWritePlatformService;
	}

	

	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		// TODO Auto-generated method stub
		return this.activationProcessWritePlatformService.createClientSimpleActivation(command,command.entityId());
	}
}
