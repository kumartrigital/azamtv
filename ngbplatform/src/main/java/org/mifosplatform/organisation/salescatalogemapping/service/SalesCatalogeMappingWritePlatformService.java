package org.mifosplatform.organisation.salescatalogemapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SalesCatalogeMappingWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateSalesCatalogeMapping(JsonCommand command, Long entityId);

	CommandProcessingResult deleteSalesCatalogeMapping(JsonCommand command, Long entityId);

}
