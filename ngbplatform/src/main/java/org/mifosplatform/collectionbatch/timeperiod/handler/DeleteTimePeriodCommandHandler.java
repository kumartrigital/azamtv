package org.mifosplatform.collectionbatch.timeperiod.handler;

import org.mifosplatform.collectionbatch.timeperiod.service.TimePeriodWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "TIMEPERIOD", action = "DELETE")
public class DeleteTimePeriodCommandHandler implements NewCommandSourceHandler{
	
	private final TimePeriodWritePlatformService timePeriodWritePlatformService;

	@Autowired
	public DeleteTimePeriodCommandHandler(TimePeriodWritePlatformService timePeriodWritePlatformService) {
		
		this.timePeriodWritePlatformService = timePeriodWritePlatformService;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return timePeriodWritePlatformService.deleteTimePeriod(command.entityId());
	
	}

}
