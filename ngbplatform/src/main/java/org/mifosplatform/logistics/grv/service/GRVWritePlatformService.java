package org.mifosplatform.logistics.grv.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface GRVWritePlatformService {
	
	CommandProcessingResult createGRV(JsonCommand command);
	
	CommandProcessingResult moveGRV(JsonCommand command);
	

}
