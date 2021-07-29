/**
 * 
 */
package org.mifosplatform.cms.eventprice.handler;

import org.mifosplatform.cms.eventprice.service.EventPriceWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link Service} Class Deleting {@link EventPricing} Handler
 * implements {@link NewCommandSourceHandler}
 * 
 * @author pavani
 *
 */
@Service
public class DeleteEventPriceCommandHandler implements NewCommandSourceHandler {
	
	@Autowired
	private EventPriceWritePlatformService eventPricingWritePlatformService;
	
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		
		return this.eventPricingWritePlatformService.deleteEventPrice(command);
	}

}
