package org.mifosplatform.organisation.broadcaster.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface BroadcasterWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateBroadcaster(JsonCommand command, Long entityId);

	CommandProcessingResult deleteBroadcaster(Long entityId);

}
