package org.mifosplatform.logistics.hardwaredeviceplan.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwarePlansWritePlatformService {

	CommandProcessingResult createHardwarePlans(JsonCommand command, Long clientId,Long clientServiceId);

}
