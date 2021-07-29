package org.mifosplatform.organisation.monetary.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.monetary.service.CurrencyWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CURRENCY", action = "CREATE")
public class CreateCurrencyCommandHandler implements NewCommandSourceHandler {

	private final CurrencyWritePlatformService writePlatformService;
	
	@Autowired
	public CreateCurrencyCommandHandler(CurrencyWritePlatformService writePlatformService) {
		this.writePlatformService = writePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writePlatformService.create(command);
	}

}
