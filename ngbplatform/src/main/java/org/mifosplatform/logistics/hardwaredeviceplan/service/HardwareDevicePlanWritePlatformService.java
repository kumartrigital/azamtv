package org.mifosplatform.logistics.hardwaredeviceplan.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwareDevicePlanWritePlatformService {

	CommandProcessingResult createHardwareDevicePlan(JsonCommand command, Long clientId);

	//CommandProcessingResult allocateHardwareDevice(JsonCommand jsonCommand);

}
