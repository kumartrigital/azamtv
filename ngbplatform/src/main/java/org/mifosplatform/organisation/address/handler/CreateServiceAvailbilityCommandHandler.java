package org.mifosplatform.organisation.address.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceWriteplatformService;
import org.mifosplatform.portfolio.service.service.ServiceMasterWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "SERVICEAVAILBILITY", action = "CREATE")
public class CreateServiceAvailbilityCommandHandler implements
		NewCommandSourceHandler {

	private final ServiceMasterWritePlatformService serviceMasterWritePlatformService;

	@Autowired
    public CreateServiceAvailbilityCommandHandler(final ServiceMasterWritePlatformService serviceMasterWritePlatformService) {
        this.serviceMasterWritePlatformService = serviceMasterWritePlatformService;
    }
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.serviceMasterWritePlatformService.addServiceAvailability(command, command.entityId());
	}

}
