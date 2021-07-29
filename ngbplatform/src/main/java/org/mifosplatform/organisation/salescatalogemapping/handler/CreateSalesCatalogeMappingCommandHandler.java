package org.mifosplatform.organisation.salescatalogemapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.salescatalogemapping.service.SalesCatalogeMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "SALESCATALOGEMAPPING", action = "CREATE")
public class CreateSalesCatalogeMappingCommandHandler implements NewCommandSourceHandler{
	
	private final SalesCatalogeMappingWritePlatformService salescatalogemappingWritePlatformService;

	@Autowired
	public CreateSalesCatalogeMappingCommandHandler(
			SalesCatalogeMappingWritePlatformService salescatalogemappingWritePlatformService) {
		this.salescatalogemappingWritePlatformService = salescatalogemappingWritePlatformService;
	}


	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.salescatalogemappingWritePlatformService.create(command);
	}

}
