package org.mifosplatform.billing.linkup.handler;

import org.mifosplatform.billing.linkup.service.LinkupAccountWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateLinkupAccountCommandHandler implements NewCommandSourceHandler{

	private final LinkupAccountWritePlatformService linkupAccountWritePlatformService; 
	
	@Autowired
	public CreateLinkupAccountCommandHandler(final LinkupAccountWritePlatformService linkupAccountWritePlatformService) {
		this.linkupAccountWritePlatformService = linkupAccountWritePlatformService;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return linkupAccountWritePlatformService.createLinkupAccount(command);
	}
}

