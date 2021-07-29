package org.mifosplatform.provisioning.networkelement.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.channel.service.ChannelWritePlatformService;
import org.mifosplatform.portfolio.note.service.NoteWritePlatformService;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "NETWORKELEMENT", action = "CREATE")
public class CreateNetworkElementCommandHandler implements NewCommandSourceHandler {
	
	private final NetworkElementWritePlatformService networkelementWritePlatformService;
	
	 @Autowired
	 public CreateNetworkElementCommandHandler(final NetworkElementWritePlatformService networkelementWritePlatformService) {
		this.networkelementWritePlatformService = networkelementWritePlatformService;
	 }
	
	 
	 @Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.networkelementWritePlatformService.create(command);
	}


}
