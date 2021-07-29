package org.mifosplatform.organisation.feemaster.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface FeeMasterWriteplatformService {

	CommandProcessingResult createFeeMaster(JsonCommand command);
	
	CommandProcessingResult updateFeeMaster(JsonCommand command);
	
	CommandProcessingResult deleteFeeMaster(Long id);
}
