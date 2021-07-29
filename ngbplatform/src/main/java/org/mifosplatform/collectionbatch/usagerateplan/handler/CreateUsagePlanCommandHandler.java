package org.mifosplatform.collectionbatch.usagerateplan.handler;

import org.mifosplatform.collectionbatch.usagerateplan.service.UsagePlanWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "USAGEPLAN", action = "CREATE")
public class CreateUsagePlanCommandHandler implements NewCommandSourceHandler {

	private final UsagePlanWritePlatformService usagePlanWritePlatformService;

	@Autowired
	public CreateUsagePlanCommandHandler(UsagePlanWritePlatformService usagePlanWritePlatformService) {
		this.usagePlanWritePlatformService = usagePlanWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.usagePlanWritePlatformService.createUsageplan(command);
	}
}
