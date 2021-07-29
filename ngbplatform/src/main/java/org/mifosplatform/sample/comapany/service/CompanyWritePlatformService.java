package org.mifosplatform.sample.comapany.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface CompanyWritePlatformService {


	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateBroadcaster(JsonCommand command, Long entityId);

	CommandProcessingResult deleteBroadcaster(Long entityId);
}
