package org.mifosplatform.portfolio.provisioncodemapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ProvisionCodeMappingWritePlatformService {


	CommandProcessingResult createProvisionCodeMapping(JsonCommand command);

	CommandProcessingResult updateProvisionCodeMapping(JsonCommand command, Long entityId);

	CommandProcessingResult deleteProvisionCodeMapping(Long entityId);

}
