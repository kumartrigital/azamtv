package org.mifosplatform.logistics.grv.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.grv.service.GRVReadPlatformService;
import org.mifosplatform.logistics.grv.service.GRVWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "GRV", action = "MOVE")
public class MoveGRVCommandHandler implements NewCommandSourceHandler  {

	private final GRVWritePlatformService gRVWritePlatformService;

	@Autowired
	public MoveGRVCommandHandler(final GRVWritePlatformService gRVWritePlatformService) {
		this.gRVWritePlatformService = gRVWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.gRVWritePlatformService.moveGRV(command);
	}
	
	
	
}
