package org.mifosplatform.collectionbatch.timemodel.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TimeModelWritePlatformService {

	CommandProcessingResult createTimemodel(JsonCommand command);
	
	CommandProcessingResult updateTimemodel(JsonCommand command, Long entityId);

	CommandProcessingResult deleteTimemodel(Long entityId);

}
