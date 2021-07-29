package org.mifosplatform.portfolio.clientdiscount.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.clientdiscount.service.ClientDiscountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CLIENTDISCOUNT", action = "DELETE")
public class DeleteClientDiscountCommandHadler implements NewCommandSourceHandler{
	

	private final ClientDiscountWritePlatformService clientDiscountWritePlatformService;

	@Autowired
	public DeleteClientDiscountCommandHadler(ClientDiscountWritePlatformService clientDiscountWritePlatformService) {
		
		this.clientDiscountWritePlatformService = clientDiscountWritePlatformService;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return clientDiscountWritePlatformService.deleteClientDiscount(command.entityId());
	
	}

}
