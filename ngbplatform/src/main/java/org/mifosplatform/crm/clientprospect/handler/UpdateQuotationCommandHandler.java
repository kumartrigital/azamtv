package org.mifosplatform.crm.clientprospect.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.QuoteWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "QUOTATION", action = "UPDATE")
public class UpdateQuotationCommandHandler implements NewCommandSourceHandler{

	
private final QuoteWritePlatformService quoteWritePlatformService;
	
	@Autowired
	public UpdateQuotationCommandHandler(final QuoteWritePlatformService quoteWritePlatformService) {
		this.quoteWritePlatformService = quoteWritePlatformService;
}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.quoteWritePlatformService.updateQuotation(command,command.entityId());
	}

}