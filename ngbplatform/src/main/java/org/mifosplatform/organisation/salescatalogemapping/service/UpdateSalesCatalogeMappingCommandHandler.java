package org.mifosplatform.organisation.salescatalogemapping.service;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "SALESCATALOGEMAPPING", action = "UPDATE")
public class UpdateSalesCatalogeMappingCommandHandler implements NewCommandSourceHandler {

	
	private final SalesCatalogeMappingWritePlatformService salescatalogeWritePlatformService;

	@Autowired
	public UpdateSalesCatalogeMappingCommandHandler(
			SalesCatalogeMappingWritePlatformService salescatalogeWritePlatformService) {
		this.salescatalogeWritePlatformService = salescatalogeWritePlatformService;
	}


	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.salescatalogeWritePlatformService.updateSalesCatalogeMapping(command,command.entityId());
	}

}
