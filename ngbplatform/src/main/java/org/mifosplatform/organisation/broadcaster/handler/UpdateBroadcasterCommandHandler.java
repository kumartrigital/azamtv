package org.mifosplatform.organisation.broadcaster.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.broadcaster.service.BroadcasterWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "BROADCASTER", action = "UPDATE")
public class UpdateBroadcasterCommandHandler implements NewCommandSourceHandler {

	
	private BroadcasterWritePlatformService broadcasterWritePlatformService;
	
	@Autowired
    public UpdateBroadcasterCommandHandler(final BroadcasterWritePlatformService broadcasterWritePlatformService) {
        this.broadcasterWritePlatformService = broadcasterWritePlatformService;
    }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.broadcasterWritePlatformService.updateBroadcaster(command,command.entityId());
	}
	

}
