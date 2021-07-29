package org.mifosplatform.organisation.voucher.handler;


import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.voucher.service.VoucherWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ashokreddy
 *
 */

@Service
public class CreateVoucherGroupCommandHandler implements NewCommandSourceHandler  {

	private final VoucherWritePlatformService writePlatformService;

    @Autowired
    public CreateVoucherGroupCommandHandler(final VoucherWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
    	return this.writePlatformService.createRandomGenerator(command);
	}
	
}
