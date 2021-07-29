package org.mifosplatform.logistics.itemdetails.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateInventoryItemsCommandHandler implements NewCommandSourceHandler {
	
	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	
	
	@Autowired
	public CreateInventoryItemsCommandHandler(final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService) {
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.inventoryItemDetailsWritePlatformService.addItem(command,command.entityId());
	
	}

}
	