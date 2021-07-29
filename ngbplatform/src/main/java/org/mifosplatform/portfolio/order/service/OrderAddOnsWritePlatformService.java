package org.mifosplatform.portfolio.order.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface OrderAddOnsWritePlatformService {

	CommandProcessingResult createOrderAddons(JsonCommand command, Long entityId);

	CommandProcessingResult disconnectOrderAddon(JsonCommand command,Long entityId);

	

}
