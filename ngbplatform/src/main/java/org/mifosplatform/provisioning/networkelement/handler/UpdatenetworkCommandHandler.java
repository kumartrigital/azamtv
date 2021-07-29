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
@CommandType(entity = "NETWORKELEMENT", action = "UPDATE")
public class UpdatenetworkCommandHandler implements NewCommandSourceHandler{
	private  NetworkElementWritePlatformService networkelementWritePlatformService;

	@Autowired
	public UpdatenetworkCommandHandler(NetworkElementWritePlatformService networkelementWritePlatformService) {
		this.networkelementWritePlatformService =networkelementWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.networkelementWritePlatformService.updateNetworkElement(command,command.entityId());
	}
	

}
