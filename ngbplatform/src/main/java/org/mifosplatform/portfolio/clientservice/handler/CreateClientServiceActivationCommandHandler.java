package org.mifosplatform.portfolio.clientservice.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CLIENTSERVICEACTIVATION", action = "CREATE")
public class CreateClientServiceActivationCommandHandler  implements NewCommandSourceHandler  {
	
	 private final ClientServiceWriteplatformService clientServiceWritePlatformService;

	    @Autowired
	    public CreateClientServiceActivationCommandHandler(final ClientServiceWriteplatformService clientServiceWritePlatformService) {
	        this.clientServiceWritePlatformService = clientServiceWritePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {
	    	
	        return this.clientServiceWritePlatformService.createClientServiceActivation(command.entityId(),command);
	    }

}
