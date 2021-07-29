package org.mifosplatform.organisation.redemption.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.redemption.service.RedemptionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRedemptionCommandHandler implements NewCommandSourceHandler {

	private final RedemptionWritePlatformService redemptionWritePlatformService;
	
	@Autowired
	public CreateRedemptionCommandHandler(final RedemptionWritePlatformService redemptionWritePlatformService){
		this.redemptionWritePlatformService = redemptionWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		
			return this.redemptionWritePlatformService.createRedemption(command);
		
	}

}
