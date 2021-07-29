package org.mifosplatform.organisation.lco.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface LCOWritePlatformService {

	CommandProcessingResult renewal(JsonCommand command);

	boolean clientBalanceCheck(Long clientId, Long orderId);

}
