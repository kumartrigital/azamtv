package org.mifosplatform.collectionbatch.usagerateplan.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface UsagePlanWritePlatformService {


	CommandProcessingResult createUsageplan(JsonCommand command);

}
