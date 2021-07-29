package org.mifosplatform.organisation.redemption.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

/**
 * Defining RedemptionWritePlatformService interface
 */
public interface RedemptionWritePlatformService {

	/**
	 * Defining createRedemption abstract method with parameter as JsonCommand
	 * 
	 * @throws Exception
	 */

	CommandProcessingResult createRedemption(JsonCommand command);
}
