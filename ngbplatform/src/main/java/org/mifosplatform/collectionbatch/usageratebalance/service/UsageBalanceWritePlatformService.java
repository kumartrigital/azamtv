package org.mifosplatform.collectionbatch.usageratebalance.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface UsageBalanceWritePlatformService {

	CommandProcessingResult createUsagebalance(JsonCommand command);

}
