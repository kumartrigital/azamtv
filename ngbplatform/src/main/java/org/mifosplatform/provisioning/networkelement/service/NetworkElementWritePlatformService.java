package org.mifosplatform.provisioning.networkelement.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface NetworkElementWritePlatformService {
	
	CommandProcessingResult updateNetworkElement(JsonCommand command, Long entityId);
	
	CommandProcessingResult create(JsonCommand command);
	
	CommandProcessingResult deleteNetworkElement(Long entityId);

}
