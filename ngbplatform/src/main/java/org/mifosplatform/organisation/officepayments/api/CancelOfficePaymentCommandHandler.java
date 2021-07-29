package org.mifosplatform.organisation.officepayments.api;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.officepayments.service.OfficePaymentsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "OFFICEPAYMENT", action = "DELETE")
public class CancelOfficePaymentCommandHandler implements NewCommandSourceHandler {
	
		private final OfficePaymentsWritePlatformService writePlatformService;
	
		@Autowired
		public CancelOfficePaymentCommandHandler(final OfficePaymentsWritePlatformService writePlatformService) {
			
			this.writePlatformService = writePlatformService;
		}

		@Transactional
		@Override
		public CommandProcessingResult processCommand(final JsonCommand command) {

			return this.writePlatformService.cancelofficepayment(command,command.entityId());
		}
		
}
