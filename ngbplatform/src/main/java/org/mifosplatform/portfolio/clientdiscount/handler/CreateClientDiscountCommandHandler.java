package org.mifosplatform.portfolio.clientdiscount.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.clientdiscount.service.ClientDiscountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@CommandType(entity = "CLIENTDISCOUNT", action = "CREATE")
public class CreateClientDiscountCommandHandler implements NewCommandSourceHandler {

	private final ClientDiscountWritePlatformService clientDiscountWritePlatformService;
	
    @Autowired
	public CreateClientDiscountCommandHandler(ClientDiscountWritePlatformService clientDiscountWritePlatformService) {
		this.clientDiscountWritePlatformService = clientDiscountWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.clientDiscountWritePlatformService.create(command);
	}

}
