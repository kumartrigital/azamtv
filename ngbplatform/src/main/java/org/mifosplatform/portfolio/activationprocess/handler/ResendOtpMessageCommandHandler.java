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
@CommandType(entity = "LEASE", action = "OTPMESSAGE")
public class ResendOtpMessageCommandHandler implements NewCommandSourceHandler  {
	private final ActivationProcessWritePlatformService activationProcessWritePlatformService;

	@Autowired
	public ResendOtpMessageCommandHandler(ActivationProcessWritePlatformService activationProcessWritePlatformService) {
		this.activationProcessWritePlatformService = activationProcessWritePlatformService;
	}
  
	@Transactional
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.activationProcessWritePlatformService.ResendOtpMessage(command);
	}

}
