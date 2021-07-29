package org.mifosplatform.portfolio.activationprocess.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwarePlanActivationProcessWritePlatformService {

	CommandProcessingResult createClientHardwarePlanActivation(JsonCommand command, Long clientId);

}
