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
@CommandType(entity = "CHANNEL", action = "UPDATE")
public class UpdateChannelCommandHandler implements  NewCommandSourceHandler{

	private  ChannelWritePlatformService channelWritePlatformService;

	@Autowired
	public UpdateChannelCommandHandler(ChannelWritePlatformService channelWritePlatformService) {
		this.channelWritePlatformService = channelWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.channelWritePlatformService.updateChannel(command,command.entityId());
	}
	

}
