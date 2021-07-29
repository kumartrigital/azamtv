package org.mifosplatform.collectionbatch.usageratequantitytier.handler;

import org.mifosplatform.collectionbatch.usageratequantitytier.service.UsageRateQuantityTierWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
@CommandType(entity = "USAGERATEQUANTITYTIER", action = "CREATE")
public class CreateUsageRateQuantityTierCommandHandler implements NewCommandSourceHandler {
	
	private final UsageRateQuantityTierWritePlatformService usageRateQuantityTierWritePlatformService;

	@Autowired
	public CreateUsageRateQuantityTierCommandHandler(UsageRateQuantityTierWritePlatformService usageRateQuantityTierWritePlatformService) {
		this.usageRateQuantityTierWritePlatformService = usageRateQuantityTierWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.usageRateQuantityTierWritePlatformService.createUsageRateQuantityTier(command);
	}
}
