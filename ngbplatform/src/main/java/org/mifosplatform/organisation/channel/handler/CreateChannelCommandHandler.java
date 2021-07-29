package org.mifosplatform.organisation.channel.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.channel.service.ChannelWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@CommandType(entity = "CHANNEL", action = "CREATE")
public class CreateChannelCommandHandler implements NewCommandSourceHandler {

	 private final ChannelWritePlatformService channelWritePlatformService;
	
	 @Autowired
	 public CreateChannelCommandHandler(final ChannelWritePlatformService channelWritePlatformService) {
		this.channelWritePlatformService = channelWritePlatformService;
	 }
	
	 
	 @Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.channelWritePlatformService.create(command);
	}
	

	
	
}
