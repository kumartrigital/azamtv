package org.mifosplatform.collectionbatch.usageratequantitytier.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface UsageRateQuantityTierWritePlatformService {

	CommandProcessingResult createUsageRateQuantityTier(JsonCommand command);

}
