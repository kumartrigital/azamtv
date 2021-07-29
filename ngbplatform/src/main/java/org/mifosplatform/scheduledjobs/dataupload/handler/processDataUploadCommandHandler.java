package org.mifosplatform.scheduledjobs.dataupload.handler;


import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.scheduledjobs.dataupload.service.DataUploadWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class processDataUploadCommandHandler implements NewCommandSourceHandler{

	private DataUploadWritePlatformService dataUploadWritePlatformService;
	
	
	@Autowired
	public processDataUploadCommandHandler(final DataUploadWritePlatformService dataUploadWritePlatformService){
		
		this.dataUploadWritePlatformService = dataUploadWritePlatformService;
	}
	
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return dataUploadWritePlatformService.processDatauploadFile(command.entityId()) ;
	}

}
