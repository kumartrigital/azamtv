package org.mifosplatform.vendoragreement.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.vendoragreement.service.VendorAgreementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@CommandType(entity = "NEWVENDORAGREEMENT", action = "UPDATE")
public class UpdateNewVendorAgreementCommandHandler implements NewCommandSourceHandler {

    private final VendorAgreementWritePlatformService writePlatformService;

    @Autowired
    public UpdateNewVendorAgreementCommandHandler(final VendorAgreementWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
    	
    	return this.writePlatformService.updateNewVendorAgreement(command,command.entityId());
    }
}

