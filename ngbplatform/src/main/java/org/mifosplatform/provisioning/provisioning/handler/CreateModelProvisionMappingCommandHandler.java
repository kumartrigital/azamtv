package org.mifosplatform.provisioning.provisioning.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.supplier.service.SupplierWritePlatformService;
import org.mifosplatform.provisioning.provisioning.service.ModelProvisionMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "MODELPROVISIONMAPPING", action = "CREATE")
public class CreateModelProvisionMappingCommandHandler implements NewCommandSourceHandler{

	final private ModelProvisionMappingWritePlatformService modelProvisionMappingWritePlatformService;
	
	@Autowired
	public CreateModelProvisionMappingCommandHandler(final ModelProvisionMappingWritePlatformService modelProvisionMappingWritePlatformService) {
		this.modelProvisionMappingWritePlatformService = modelProvisionMappingWritePlatformService;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return modelProvisionMappingWritePlatformService.createModelProvisionMapping(command);
	}

}
