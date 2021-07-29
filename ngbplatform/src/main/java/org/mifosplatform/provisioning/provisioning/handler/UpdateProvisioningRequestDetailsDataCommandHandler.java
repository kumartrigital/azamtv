package org.mifosplatform.provisioning.provisioning.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "PROVISIONINGDETAILSMESSAGE", action = "UPDATE")
public class UpdateProvisioningRequestDetailsDataCommandHandler implements NewCommandSourceHandler{
	
	private final ProvisioningWritePlatformService writePlatformService;
	
	 @Autowired
	    public UpdateProvisioningRequestDetailsDataCommandHandler(final ProvisioningWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }
	 
	 @Transactional
	 @Override
		public CommandProcessingResult processCommand(JsonCommand command) {
	       return this.writePlatformService.updateProvisioningRequestDetailsData(command,command.entityId());
		}

}
