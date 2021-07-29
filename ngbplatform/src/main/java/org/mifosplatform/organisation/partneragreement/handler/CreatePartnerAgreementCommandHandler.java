package org.mifosplatform.organisation.partneragreement.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.partneragreement.service.PartnersAgreementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

	@Service
	public class CreatePartnerAgreementCommandHandler implements NewCommandSourceHandler {

	    private final PartnersAgreementWritePlatformService writePlatformService;

	    @Autowired
	    public CreatePartnerAgreementCommandHandler(final PartnersAgreementWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {

	        return this.writePlatformService.createNewPartnerAgreement(command);
	    }
	}


