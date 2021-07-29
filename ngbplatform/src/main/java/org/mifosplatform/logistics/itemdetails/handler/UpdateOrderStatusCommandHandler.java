package org.mifosplatform.logistics.itemdetails.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.grn.service.GrnDetailsWritePlatformService;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "GRNORDERSTATUS", action = "UPDATE")
public class UpdateOrderStatusCommandHandler implements NewCommandSourceHandler{

	private GrnDetailsWritePlatformService grnDetailsWritePlatformService;
	
	
	@Autowired
	public UpdateOrderStatusCommandHandler(final GrnDetailsWritePlatformService grnDetailsWritePlatformService){
		this.grnDetailsWritePlatformService = grnDetailsWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return grnDetailsWritePlatformService.updateGrnOrderStatus(1,command.entityId());
	}

}
