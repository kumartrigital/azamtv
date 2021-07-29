package org.mifosplatform.provisioning.provisioning.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.provisioning.service.ModelProvisionMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "MODELPROVISIONMAPPING", action = "UPDATE")
public class UpdateModelProvisionMappingCommandHandler implements NewCommandSourceHandler{

	final private ModelProvisionMappingWritePlatformService modelProvisionMappingWritePlatformService;
	
	@Autowired
	public UpdateModelProvisionMappingCommandHandler(final ModelProvisionMappingWritePlatformService modelProvisionMappingWritePlatformService) {
		this.modelProvisionMappingWritePlatformService = modelProvisionMappingWritePlatformService;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return modelProvisionMappingWritePlatformService.updateModelProvisionMapping(command, command.entityId());
	}
}
