package org.mifosplatform.portfolio.client.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ClientBillInfoWritePlatformService {
	
	CommandProcessingResult updateClientBillInfo(JsonCommand command, Long entityId);
	
}
