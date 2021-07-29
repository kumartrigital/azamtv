package org.mifosplatform.portfolio.client.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.service.ClientBillInfoWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CLIENTBILLPROFILE", action = "UPDATE")
public class UpdateClientBillInfoCommandHandler implements NewCommandSourceHandler {

	
	private ClientBillInfoWritePlatformService clientbillinfoWritePlatformService;
	
	@Autowired
	public UpdateClientBillInfoCommandHandler(ClientBillInfoWritePlatformService clientbillinfoWritePlatformService) {
		this.clientbillinfoWritePlatformService = clientbillinfoWritePlatformService;
	}


	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.clientbillinfoWritePlatformService.updateClientBillInfo(command,command.entityId());
	}
	
}
