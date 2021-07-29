package org.mifosplatform.portfolio.order.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "OSDMESSAGE", action = "CREATE")
public class OsdBrmMessageOrderCommandHandler implements NewCommandSourceHandler{
	
	 private final OrderWritePlatformService writePlatformService;
	
	@Autowired
    public OsdBrmMessageOrderCommandHandler(final OrderWritePlatformService writePlatformService) {
		 this.writePlatformService = writePlatformService;
    }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		try {
			return this.writePlatformService.Osdmessage(command,command.entityId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return CommandProcessingResult.empty();
		}
	}
	
	
}
