package org.mifosplatform.logistics.grv.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.grv.service.GRVWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "GRV", action = "CREATE")
public class CreateGRVCommandHandler implements NewCommandSourceHandler{
	
	
private final GRVWritePlatformService grvWritePlatformService;
	
	@Autowired
	public CreateGRVCommandHandler(final GRVWritePlatformService grvWritePlatformService) {
		this.grvWritePlatformService = grvWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return grvWritePlatformService.createGRV(command);
	}
	
	

}
