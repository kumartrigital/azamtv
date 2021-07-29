package org.mifosplatform.portfolio.clientdiscount.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ClientDiscountWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateClientDiscount(JsonCommand command, Long entityId);

	CommandProcessingResult deleteClientDiscount(Long entityId);

}
