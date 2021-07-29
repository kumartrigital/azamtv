/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.logistics.itemdetails.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.mifosplatform.workflow.eventactionmapping.service.EventActionMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateInventoryItemsCommandHandler implements NewCommandSourceHandler {

	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;

    @Autowired
    public UpdateInventoryItemsCommandHandler(final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService) {
    	this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.inventoryItemDetailsWritePlatformService.updateItem(command.entityId(), command);
    }
}