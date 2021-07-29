package org.mifosplatform.collectionbatch.timemodel.handler;

import org.mifosplatform.collectionbatch.timemodel.service.TimeModelWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "TIMEMODEL", action = "UPDATE")
public class UpdateTimeModelCommandHandler implements NewCommandSourceHandler{
	private final TimeModelWritePlatformService timeModelWritePlatformService;

	@Autowired
	public UpdateTimeModelCommandHandler(TimeModelWritePlatformService timeModelWritePlatformService) {
		this.timeModelWritePlatformService = timeModelWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.timeModelWritePlatformService.updateTimemodel(command,command.entityId());
	}
}
