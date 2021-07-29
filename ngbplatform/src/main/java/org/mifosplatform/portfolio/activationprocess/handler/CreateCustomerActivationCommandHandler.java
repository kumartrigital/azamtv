package org.mifosplatform.portfolio.activationprocess.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.activationprocess.service.ActivationProcessWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CUSTOMERACTIVATION", action = "CREATE")
public class CreateCustomerActivationCommandHandler implements
		NewCommandSourceHandler {
	private final ActivationProcessWritePlatformService activationProcessWritePlatformService;

	@Autowired
	public CreateCustomerActivationCommandHandler(ActivationProcessWritePlatformService activationProcessWritePlatformService) {
		this.activationProcessWritePlatformService = activationProcessWritePlatformService;
	}
	
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.activationProcessWritePlatformService.createCustomerActivation(command);
	}

}
