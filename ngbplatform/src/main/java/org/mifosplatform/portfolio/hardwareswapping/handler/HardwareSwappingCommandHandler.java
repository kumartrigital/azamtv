package org.mifosplatform.portfolio.hardwareswapping.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.hardwareswapping.service.HardwareSwappingWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HardwareSwappingCommandHandler  implements NewCommandSourceHandler {

	private final HardwareSwappingWriteplatformService writePlatformService;
	  
	  @Autowired
	    public HardwareSwappingCommandHandler(HardwareSwappingWriteplatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	       
	    }
	
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		
		return writePlatformService.doHardWareSwapping(command.entityId(),command);
	}

}
