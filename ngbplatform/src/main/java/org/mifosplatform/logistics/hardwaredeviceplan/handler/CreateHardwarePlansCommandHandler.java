package org.mifosplatform.logistics.hardwaredeviceplan.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.hardwaredeviceplan.service.HardwarePlansWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "HARDWAREPLAN", action = "CREATE")
public class CreateHardwarePlansCommandHandler implements NewCommandSourceHandler {
	
	private final HardwarePlansWritePlatformService hardwarePlansWritePlatformService;

	@Autowired
	public CreateHardwarePlansCommandHandler(HardwarePlansWritePlatformService hardwarePlansWritePlatformService) {
		
		this.hardwarePlansWritePlatformService = hardwarePlansWritePlatformService;
	
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.hardwarePlansWritePlatformService.createHardwarePlans(command,command.entityId(),command.subentityId());
	}

}
