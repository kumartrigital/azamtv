package org.mifosplatform.provisioning.provisioning.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ModelProvisionMappingWritePlatformService {

	CommandProcessingResult createModelProvisionMapping(JsonCommand command);

	CommandProcessingResult updateModelProvisionMapping(JsonCommand command, Long entityId);

	CommandProcessingResult deleteModelProvisionMapping(Long entityId);
	
}
