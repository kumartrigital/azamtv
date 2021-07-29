package org.mifosplatform.portfolio.activationprocess.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.activationprocess.service.ActivationProcessWritePlatformService;
import org.mifosplatform.portfolio.activationprocess.service.HardwarePlanActivationProcessWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CLIENTHARDWAREPLANACTIVATION", action = "CREATE")
public class CreateClientHardwarePlanActivationCommandHandler implements NewCommandSourceHandler{

	
	private final HardwarePlanActivationProcessWritePlatformService hardwarePlanactivationProcessWritePlatformService;

	@Autowired
	public CreateClientHardwarePlanActivationCommandHandler(
			HardwarePlanActivationProcessWritePlatformService hardwarePlanactivationProcessWritePlatformService) {
		this.hardwarePlanactivationProcessWritePlatformService = hardwarePlanactivationProcessWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.hardwarePlanactivationProcessWritePlatformService.createClientHardwarePlanActivation(command,command.entityId());
	}

}
