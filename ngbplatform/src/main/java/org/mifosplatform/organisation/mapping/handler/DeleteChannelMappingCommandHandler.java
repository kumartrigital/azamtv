package org.mifosplatform.organisation.mapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.mapping.service.ChannelMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CHANNELMAPPING", action = "DELETE")
public class DeleteChannelMappingCommandHandler implements NewCommandSourceHandler {

	
	
	private final ChannelMappingWritePlatformService channelmappingWritePlatformService;
	
	@Autowired
	public DeleteChannelMappingCommandHandler(final ChannelMappingWritePlatformService channelmappingWritePlatformService) {
		this.channelmappingWritePlatformService = channelmappingWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return channelmappingWritePlatformService.deleteChannelMapping(command.entityId());
	}

}
