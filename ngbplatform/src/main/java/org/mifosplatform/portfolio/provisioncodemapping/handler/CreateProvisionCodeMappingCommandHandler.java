package org.mifosplatform.portfolio.provisioncodemapping.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.provisioncodemapping.service.ProvisionCodeMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@CommandType(entity = "PROVISIONCODEMAPPING", action = "CREATE")
public class CreateProvisionCodeMappingCommandHandler implements NewCommandSourceHandler {

	private final ProvisionCodeMappingWritePlatformService provisionCodeMappingWritePlatformService;

	@Autowired
	public CreateProvisionCodeMappingCommandHandler(
			ProvisionCodeMappingWritePlatformService provisionCodeMappingWritePlatformService) {
		this.provisionCodeMappingWritePlatformService = provisionCodeMappingWritePlatformService;
	}


	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.provisionCodeMappingWritePlatformService.createProvisionCodeMapping(command);
	}

}
