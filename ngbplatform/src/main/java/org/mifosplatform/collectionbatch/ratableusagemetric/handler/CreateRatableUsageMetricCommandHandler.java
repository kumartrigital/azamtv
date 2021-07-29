package org.mifosplatform.collectionbatch.ratableusagemetric.handler;

import org.mifosplatform.collectionbatch.ratableusagemetric.service.RatableUsageMetricWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@CommandType(entity = "RATABLEUSAGEMETRIC", action = "CREATE")
public class CreateRatableUsageMetricCommandHandler implements NewCommandSourceHandler{
		
		
		 private final RatableUsageMetricWritePlatformService ratableUsageMetricWritePlatformService;
		 
		 
		 
		@Autowired
		public CreateRatableUsageMetricCommandHandler(RatableUsageMetricWritePlatformService ratableUsageMetricWritePlatformService) {
			this.ratableUsageMetricWritePlatformService = ratableUsageMetricWritePlatformService;
		}


		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
			return this.ratableUsageMetricWritePlatformService.createRatableUsageMetric(command);
		}

}
