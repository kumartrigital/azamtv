package org.mifosplatform.crm.clientprospect.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.QuoteWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "QUOTATION", action = "DELETE")
public class DeleteQuotationCommandHandler implements NewCommandSourceHandler {
	
	private final QuoteWritePlatformService quoteWritePlatformService;
	
	@Autowired
	public DeleteQuotationCommandHandler(final QuoteWritePlatformService quoteWritePlatformService) {
		this.quoteWritePlatformService = quoteWritePlatformService;

	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return quoteWritePlatformService.deleteQuotation(command,command.entityId());
	}
}
