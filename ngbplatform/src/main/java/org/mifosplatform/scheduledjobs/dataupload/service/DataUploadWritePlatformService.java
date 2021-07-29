package org.mifosplatform.scheduledjobs.dataupload.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.scheduledjobs.dataupload.command.DataUploadCommand;


public interface DataUploadWritePlatformService {


	CommandProcessingResult addItem(DataUploadCommand command);
	
	
	CommandProcessingResult processDatauploadFile(Long fileId);

	
	
	

}
