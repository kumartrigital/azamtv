package org.mifosplatform.logistics.mrn.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.mrn.service.MRNDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "MRNCARTON", action = "MOVE")
public class MoveMRNCartonCommandHandler  implements NewCommandSourceHandler{

	
	private final MRNDetailsWritePlatformService mrnDetailsWritePlatformService;
	
	@Autowired
	public MoveMRNCartonCommandHandler(final MRNDetailsWritePlatformService mrnDetailsWritePlatformService) {
		this.mrnDetailsWritePlatformService = mrnDetailsWritePlatformService;
	}
	
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return mrnDetailsWritePlatformService.movemrncarton(command);
	}
}
	
	

