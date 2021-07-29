package org.mifosplatform.cms.eventprice.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

/**
 * Interface for {@link EventPricing} Write Service
 * 
 * @author pavani
 *
 */
public interface EventPriceWritePlatformService {

	/**
	 * Method for Creating {@link EventPricing}
	 * 
	 * @param command
	 * @return
	 */
	CommandProcessingResult createEventPrice(JsonCommand command);
	
	/**
	 * Method for Updating {@link EventPricing}
	 * 
	 * @param command
	 * @return
	 */
	CommandProcessingResult updateEventPrice(JsonCommand command);
	
	/**
	 * Method for Deleting EventPricing
	 * 
	 * @param command
	 * @return
	 */
	CommandProcessingResult deleteEventPrice(JsonCommand command);
	
}
