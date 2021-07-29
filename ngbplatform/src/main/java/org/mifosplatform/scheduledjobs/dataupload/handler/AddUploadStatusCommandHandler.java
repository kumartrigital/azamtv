package org.mifosplatform.scheduledjobs.dataupload.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.scheduledjobs.dataupload.service.DataUploadWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddUploadStatusCommandHandler implements NewCommandSourceHandler {
	
	private final DataUploadWritePlatformService uploadStatusWritePlatformService;
	
	
	@Autowired
	public AddUploadStatusCommandHandler(final DataUploadWritePlatformService uploadStatusWritePlatformService) {
		this.uploadStatusWritePlatformService = uploadStatusWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		//return this.inventoryItemDetailsWritePlatformService.addItem(command);
		return null;
	}

}
