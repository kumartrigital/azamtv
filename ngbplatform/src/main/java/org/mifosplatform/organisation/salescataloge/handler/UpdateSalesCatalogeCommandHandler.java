package org.mifosplatform.organisation.salescataloge.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.salescataloge.service.SalesCatalogeWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "SALESCATALOGE", action = "UPDATE")
public class UpdateSalesCatalogeCommandHandler implements  NewCommandSourceHandler{

	private final SalesCatalogeWritePlatformService salescatalogeWritePlatformService;

	@Autowired
	public UpdateSalesCatalogeCommandHandler(SalesCatalogeWritePlatformService salescatalogeWritePlatformService) {
		this.salescatalogeWritePlatformService = salescatalogeWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.salescatalogeWritePlatformService.updateSalesCataloge(command,command.entityId());
	}

}
