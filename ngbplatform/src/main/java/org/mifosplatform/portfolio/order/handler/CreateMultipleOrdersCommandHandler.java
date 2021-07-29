package org.mifosplatform.portfolio.order.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "MULTIPLEORDERS", action = "CREATE")
public class CreateMultipleOrdersCommandHandler implements
		NewCommandSourceHandler {

	private final OrderWritePlatformService writePlatformService;

    @Autowired
    public CreateMultipleOrdersCommandHandler(final OrderWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writePlatformService.createMultipleOrder(command.entityId(), command, null);
	}

}
