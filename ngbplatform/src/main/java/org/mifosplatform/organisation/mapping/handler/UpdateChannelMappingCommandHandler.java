package org.mifosplatform.organisation.mapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.channel.service.ChannelWritePlatformService;
import org.mifosplatform.organisation.mapping.service.ChannelMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CHANNELMAPPING", action = "UPDATE")
public class UpdateChannelMappingCommandHandler implements  NewCommandSourceHandler{

	private  ChannelMappingWritePlatformService channelMappingWritePlatformService;
	
	@Autowired
	public UpdateChannelMappingCommandHandler(ChannelMappingWritePlatformService channelMappingWritePlatformService) {
		this.channelMappingWritePlatformService = channelMappingWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.channelMappingWritePlatformService.updateChannelMapping(command,command.entityId());
	}

}
