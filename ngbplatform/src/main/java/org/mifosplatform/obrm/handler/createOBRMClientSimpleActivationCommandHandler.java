package org.mifosplatform.obrm.handler;

import org.json.JSONException;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.obrm.service.ObrmWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "OBRMCLIENTSIMPLEACTIVATION", action = "CREATE")

public class createOBRMClientSimpleActivationCommandHandler implements NewCommandSourceHandler {

private final ObrmWritePlatformService obrmWritePlatformService;	
	
	@Autowired	
	public createOBRMClientSimpleActivationCommandHandler(ObrmWritePlatformService obrmWritePlatformService) {
	
		this.obrmWritePlatformService = obrmWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.obrmWritePlatformService.createClientSimpleActivation(command);
	}
	
	
}
