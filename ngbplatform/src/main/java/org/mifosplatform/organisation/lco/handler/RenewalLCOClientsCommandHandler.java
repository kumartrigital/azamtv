package org.mifosplatform.organisation.lco.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.lco.service.LCOWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LCO", action = "RENEWAL")
public class RenewalLCOClientsCommandHandler implements NewCommandSourceHandler {

	private final LCOWritePlatformService lCOWritePlatformService; 	
	 @Autowired
	 public RenewalLCOClientsCommandHandler(LCOWritePlatformService lCOWritePlatformService) {
		 this.lCOWritePlatformService=lCOWritePlatformService;
	 }
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return  lCOWritePlatformService.renewal(command);
	}

}
