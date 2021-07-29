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
@CommandType(entity = "BROADCASTER", action = "DELETE")
public class DeleteBroadcasterCommandHandler implements NewCommandSourceHandler {

	private final BroadcasterWritePlatformService broadcasterWritePlatformService;
	
	@Autowired
	public DeleteBroadcasterCommandHandler(final BroadcasterWritePlatformService broadcasterWritePlatformService) {
		this.broadcasterWritePlatformService = broadcasterWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return broadcasterWritePlatformService.deleteBroadcaster(command.entityId());
	}

}
