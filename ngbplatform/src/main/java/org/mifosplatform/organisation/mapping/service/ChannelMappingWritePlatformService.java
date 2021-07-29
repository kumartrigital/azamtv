package org.mifosplatform.organisation.mapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ChannelMappingWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateChannelMapping(JsonCommand command, Long entityId);
	
	CommandProcessingResult deleteChannelMapping(Long entityId);

}
