package org.mifosplatform.provisioning.networkelement.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

import org.mifosplatform.provisioning.networkelement.service.NetworkElementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "NETWORKELEMENT", action = "DELETE")
public class DeleteNetworkElementCommandHandler implements NewCommandSourceHandler{
	
	private final NetworkElementWritePlatformService networkelementWritePlatformService;

	@Autowired
	public DeleteNetworkElementCommandHandler(NetworkElementWritePlatformService networkelementWritePlatformService) {
		
		this.networkelementWritePlatformService = networkelementWritePlatformService;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return networkelementWritePlatformService.deleteNetworkElement(command.entityId());
	
	}

}
