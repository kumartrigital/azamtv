package org.mifosplatform.obrm.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.obrm.service.ObrmWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "OBRMCLIENT", action = "CREATE")
public class createOBRMClientCommandHandler implements NewCommandSourceHandler {

	private final ObrmWritePlatformService obrmWritePlatformService;	
	
	@Autowired	
	public createOBRMClientCommandHandler(ObrmWritePlatformService obrmWritePlatformService) {
		super();
		this.obrmWritePlatformService = obrmWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.obrmWritePlatformService.createClient(command);
	}
	
	

}
