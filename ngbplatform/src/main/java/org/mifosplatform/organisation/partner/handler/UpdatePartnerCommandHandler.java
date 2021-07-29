package org.mifosplatform.organisation.partner.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.partner.service.PartnersWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePartnerCommandHandler implements NewCommandSourceHandler {
	
	 private final PartnersWritePlatformService writePlatformService;
	 
	@Autowired
    public UpdatePartnerCommandHandler(final PartnersWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.writePlatformService.updatePartner(command, command.entityId());
	}

}
