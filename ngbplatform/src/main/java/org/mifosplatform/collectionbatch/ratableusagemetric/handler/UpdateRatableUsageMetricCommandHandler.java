package org.mifosplatform.collectionbatch.ratableusagemetric.handler;

import org.mifosplatform.collectionbatch.ratableusagemetric.service.RatableUsageMetricWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@CommandType(entity = "RATABLEUSAGEMETRIC", action = "UPDATE")
public class UpdateRatableUsageMetricCommandHandler implements NewCommandSourceHandler {

private RatableUsageMetricWritePlatformService ratableUsageMetricWritePlatformService;
	
	@Autowired
    public UpdateRatableUsageMetricCommandHandler(final RatableUsageMetricWritePlatformService ratableUsageMetricWritePlatformService) {
        this.ratableUsageMetricWritePlatformService = ratableUsageMetricWritePlatformService;
    }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.ratableUsageMetricWritePlatformService.updateRatableUsageMetric(command,command.entityId());
	}
	

}
