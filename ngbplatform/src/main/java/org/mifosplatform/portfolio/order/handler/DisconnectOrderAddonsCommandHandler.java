package org.mifosplatform.portfolio.order.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.service.OrderAddOnsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisconnectOrderAddonsCommandHandler implements NewCommandSourceHandler{
	
	private final OrderAddOnsWritePlatformService orderAddOnsWritePlatformService;

	@Autowired
	public DisconnectOrderAddonsCommandHandler(final OrderAddOnsWritePlatformService  addOnsWritePlatformService){
		this.orderAddOnsWritePlatformService = addOnsWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
        return this.orderAddOnsWritePlatformService.disconnectOrderAddon(command,command.entityId());      
	}

}
