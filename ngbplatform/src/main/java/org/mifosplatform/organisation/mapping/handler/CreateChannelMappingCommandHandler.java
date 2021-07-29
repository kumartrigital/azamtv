package org.mifosplatform.organisation.mapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.mapping.service.ChannelMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CHANNELMAPPING", action = "CREATE")
public class CreateChannelMappingCommandHandler implements NewCommandSourceHandler {

	
	private final ChannelMappingWritePlatformService channelmappingWritePlatformService;
	
	@Autowired
	public CreateChannelMappingCommandHandler(final  ChannelMappingWritePlatformService channelmappingWritePlatformService) {
		this.channelmappingWritePlatformService = channelmappingWritePlatformService;
	}


	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.channelmappingWritePlatformService.create(command);
	}

}
