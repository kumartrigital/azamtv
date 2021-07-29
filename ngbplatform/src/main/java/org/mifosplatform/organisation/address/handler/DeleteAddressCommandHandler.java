package org.mifosplatform.organisation.address.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.address.service.AddressWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteAddressCommandHandler implements NewCommandSourceHandler{
	
	private final AddressWritePlatformService addressWritePlatformService;
	
@Autowired
public DeleteAddressCommandHandler(final AddressWritePlatformService addressWritePlatformService){
 this.addressWritePlatformService = addressWritePlatformService;	
	
}

@Transactional
@Override
public CommandProcessingResult processCommand(JsonCommand command) {
     return this.addressWritePlatformService.deleteAddress(command.entityId(),command);

}

}
