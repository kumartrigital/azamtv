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
@CommandType(entity = "CLIENTSERVICE", action = "UPDATE")
public class UpdateServiceparameterCommandHandler implements NewCommandSourceHandler {

	
	private ClientServiceWriteplatformService clientServiceWriteplatformService;
	
	@Autowired
    public UpdateServiceparameterCommandHandler(final ClientServiceWriteplatformService clientServiceWriteplatformService) {
        this.clientServiceWriteplatformService = clientServiceWriteplatformService;
    }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.clientServiceWriteplatformService.updateServiceparameter(command,command.entityId());
	}

}
