package org.mifosplatform.obrm.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.obrm.service.ObrmWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "OBRMOFFICE", action = "CREATE")
public class CreateOBRMOfficeCommandHandler implements NewCommandSourceHandler {

	private final ObrmWritePlatformService obrmWritePlatformService;	
	
	@Autowired	
	public CreateOBRMOfficeCommandHandler(ObrmWritePlatformService obrmWritePlatformService) {
		super();
		this.obrmWritePlatformService = obrmWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.obrmWritePlatformService.createOffice(command);
	}
	
}
