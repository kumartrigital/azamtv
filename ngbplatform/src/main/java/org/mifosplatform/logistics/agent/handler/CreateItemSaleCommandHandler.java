package org.mifosplatform.logistics.agent.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.agent.service.ItemSaleWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
@CommandType(entity = "ITEMSALE", action = "CREATE")
public class CreateItemSaleCommandHandler implements NewCommandSourceHandler {
	
    
	private final ItemSaleWriteplatformService agentWriteplatformService;
	
	@Autowired
	public CreateItemSaleCommandHandler(final ItemSaleWriteplatformService agentWriteplatformService) {
		this.agentWriteplatformService=agentWriteplatformService;
	}

	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		return this.agentWriteplatformService.createNewItemSale(command);
	}

}
