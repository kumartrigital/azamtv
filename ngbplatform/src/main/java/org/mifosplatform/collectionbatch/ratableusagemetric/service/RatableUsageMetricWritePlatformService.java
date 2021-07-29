package org.mifosplatform.collectionbatch.ratableusagemetric.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface RatableUsageMetricWritePlatformService {

	CommandProcessingResult createRatableUsageMetric(JsonCommand command);

	CommandProcessingResult updateRatableUsageMetric(JsonCommand command, Long entityId);

	//CommandProcessingResult deleteRatableUsageMetric(Long entityId);

}
