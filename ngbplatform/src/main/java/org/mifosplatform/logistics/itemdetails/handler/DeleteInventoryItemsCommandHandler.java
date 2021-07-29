package org.mifosplatform.logistics.itemdetails.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteInventoryItemsCommandHandler implements
		NewCommandSourceHandler {

	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;

	@Autowired
	public DeleteInventoryItemsCommandHandler(
			final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService) {
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {

		return this.inventoryItemDetailsWritePlatformService.deleteItem(
				command.entityId(), command);
	}
}
