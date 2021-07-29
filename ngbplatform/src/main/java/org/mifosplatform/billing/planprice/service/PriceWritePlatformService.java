package org.mifosplatform.billing.planprice.service;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PriceWritePlatformService {


	CommandProcessingResult createPricing(Long planId,JsonCommand command);

	CommandProcessingResult updatePrice(Long priceId, JsonCommand command);

	CommandProcessingResult deletePrice(Long entityId);

	


}
