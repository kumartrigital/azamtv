package org.mifosplatform.celcom.handler;

import org.mifosplatform.celcom.service.CelcomWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CELCOMPLAN", action = "SYNC")
public class SyncPlanCelcomCommandHandler implements NewCommandSourceHandler {
	
	
	private final CelcomWritePlatformService celcomWritePlatformService;	
	
	@Autowired	
	public  SyncPlanCelcomCommandHandler(final CelcomWritePlatformService celcomWritePlatformService) {
		this.celcomWritePlatformService = celcomWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.celcomWritePlatformService.syncplan(command);
	}

}

