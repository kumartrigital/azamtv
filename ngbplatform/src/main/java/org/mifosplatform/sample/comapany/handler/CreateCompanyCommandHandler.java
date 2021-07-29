package org.mifosplatform.sample.comapany.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.broadcaster.service.BroadcasterWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "COMPANYREGISTRATION", action = "CREATE")
public class CreateCompanyCommandHandler implements NewCommandSourceHandler{
	
	
	 private final BroadcasterWritePlatformService broadcasterWriteplatformService;
	 
	 
	 
	@Autowired
	public CreateCompanyCommandHandler(BroadcasterWritePlatformService broadcasterWriteplatformService) {
		this.broadcasterWriteplatformService = broadcasterWriteplatformService;
	}


	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.broadcasterWriteplatformService.create(command);
	}
	
	
	
	

}
