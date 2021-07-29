package org.mifosplatform.logistics.supplier.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.supplier.service.SupplierWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSupplierCommandHandler implements NewCommandSourceHandler {

	
	private SupplierWritePlatformService supplierWritePlatformService;
	
	@Autowired
    public UpdateSupplierCommandHandler(final SupplierWritePlatformService supplierWritePlatformService) {
        this.supplierWritePlatformService = supplierWritePlatformService;
    }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.supplierWritePlatformService.updateSupplier(command,command.entityId());
	}

}

