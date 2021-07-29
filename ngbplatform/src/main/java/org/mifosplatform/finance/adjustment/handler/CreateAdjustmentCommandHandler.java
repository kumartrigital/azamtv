package org.mifosplatform.finance.adjustment.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.adjustment.service.AdjustmentWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateAdjustmentCommandHandler  implements NewCommandSourceHandler{

	  private final AdjustmentWritePlatformService writePlatformService;
		  
	  @Autowired
	    public CreateAdjustmentCommandHandler(final AdjustmentWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	       
	    }
	
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		
		return writePlatformService.createAdjustment(command);
		
	}

}
