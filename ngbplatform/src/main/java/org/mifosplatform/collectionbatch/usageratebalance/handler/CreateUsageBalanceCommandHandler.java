package org.mifosplatform.collectionbatch.usageratebalance.handler;

import org.mifosplatform.collectionbatch.usageratebalance.service.UsageBalanceWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "USAGEBALANCE", action = "CREATE")
public class CreateUsageBalanceCommandHandler implements NewCommandSourceHandler{

	private final UsageBalanceWritePlatformService usageBalanceWritePlatformService;

	@Autowired
	public CreateUsageBalanceCommandHandler(UsageBalanceWritePlatformService usageBalanceWritePlatformService) {
		this.usageBalanceWritePlatformService = usageBalanceWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.usageBalanceWritePlatformService.createUsagebalance(command);
	}
}
