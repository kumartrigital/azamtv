package org.mifosplatform.logistics.hardwaredeviceplan.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.hardwaredeviceplan.service.HardwareDevicePlanWritePlatformService;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CREATEHARDWAREDEVICEPLAN", action = "CREATE")
public class CreateHardwareDevicePlanCommandHandler implements NewCommandSourceHandler {

    private final HardwareDevicePlanWritePlatformService hardwareDevicePlanwritePlatformService;

    @Autowired
    public CreateHardwareDevicePlanCommandHandler(final HardwareDevicePlanWritePlatformService hardwareDevicePlanwritePlatformService) {
        this.hardwareDevicePlanwritePlatformService = hardwareDevicePlanwritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.hardwareDevicePlanwritePlatformService.createHardwareDevicePlan(command,command.entityId());
    }
}
