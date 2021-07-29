package org.mifosplatform.billing.taxmapping.handler;


import org.mifosplatform.billing.taxmapping.service.TaxMapWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteTaxMapCommandHandler implements NewCommandSourceHandler{
	
	 private final  TaxMapWritePlatformService taxMapWritePlatformService;
	 
	 @Autowired
	    public DeleteTaxMapCommandHandler(final TaxMapWritePlatformService taxMapWritePlatformService) {
	        this.taxMapWritePlatformService = taxMapWritePlatformService;
	    }
	 
	 @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {

	        return this.taxMapWritePlatformService.DeleteTaxMap(command.entityId());
	    }
}
