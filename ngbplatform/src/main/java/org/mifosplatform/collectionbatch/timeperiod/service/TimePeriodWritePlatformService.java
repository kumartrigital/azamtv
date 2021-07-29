package org.mifosplatform.collectionbatch.timeperiod.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TimePeriodWritePlatformService {

	CommandProcessingResult createTimeperiod(JsonCommand command);

	CommandProcessingResult updateTimeperiod(JsonCommand command, Long entityId);

	CommandProcessingResult deleteTimePeriod(Long entityId);

}
