package org.mifosplatform.freeradius.radius.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface RadiusWritePlatformService {

	CommandProcessingResult updateRadService(Long entityId, JsonCommand command);

	CommandProcessingResult deleteRadService(Long entityId);

}
