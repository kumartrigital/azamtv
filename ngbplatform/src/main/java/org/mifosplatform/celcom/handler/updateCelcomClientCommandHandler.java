package org.mifosplatform.celcom.handler;

import org.mifosplatform.celcom.service.CelcomWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CELCOMCLIENT", action = "UPDATE")
public class updateCelcomClientCommandHandler implements NewCommandSourceHandler {
	
	private final CelcomWritePlatformService celcomWritePlatformService;	
	
	
	 @Autowired	
		public updateCelcomClientCommandHandler(final CelcomWritePlatformService celcomWritePlatformService) {
			this.celcomWritePlatformService = celcomWritePlatformService;
		}
	
	
	     public CommandProcessingResult processCommand(final JsonCommand command) {
	        return this.celcomWritePlatformService.updateCelcomClient(command.entityId(), command);
	    }
	 

}
