package org.mifosplatform.logistics.itemdetails.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.grn.service.GrnDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditInventoryGrnCommandHandler implements NewCommandSourceHandler{

	private GrnDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	
	
	@Autowired
	public EditInventoryGrnCommandHandler(final GrnDetailsWritePlatformService inventoryItemDetailsWritePlatformService){
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
	}
	
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return inventoryItemDetailsWritePlatformService.editGrnDetails(command,command.entityId());
	}

}
