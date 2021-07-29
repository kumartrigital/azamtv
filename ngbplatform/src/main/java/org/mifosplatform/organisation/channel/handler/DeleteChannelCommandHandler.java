package org.mifosplatform.organisation.channel.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.channel.service.ChannelWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CHANNEL", action = "DELETE")
public class DeleteChannelCommandHandler implements NewCommandSourceHandler{
	

	private final ChannelWritePlatformService channelWritePlatformService;

	@Autowired
	public DeleteChannelCommandHandler(ChannelWritePlatformService channelWritePlatformService) {
		
		this.channelWritePlatformService = channelWritePlatformService;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return channelWritePlatformService.deleteChannel(command.entityId());
	
	}

}
