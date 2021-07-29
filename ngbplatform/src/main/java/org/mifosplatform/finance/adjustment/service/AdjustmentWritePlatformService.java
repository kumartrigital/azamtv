package org.mifosplatform.finance.adjustment.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface AdjustmentWritePlatformService {
	

	CommandProcessingResult createAdjustment(JsonCommand command);
	
}
