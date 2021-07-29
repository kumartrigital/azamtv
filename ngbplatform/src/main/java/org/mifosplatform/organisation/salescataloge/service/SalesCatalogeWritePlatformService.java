package org.mifosplatform.organisation.salescataloge.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SalesCatalogeWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateSalesCataloge(JsonCommand command, Long entityId);

	CommandProcessingResult deleteSalesCataloge(JsonCommand command, Long entityId);

}
