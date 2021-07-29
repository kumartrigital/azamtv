package org.mifosplatform.organisation.channel.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface ChannelWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateChannel(JsonCommand command, Long entityId);

	CommandProcessingResult deleteChannel(Long entityId);
	

}
