package org.mifosplatform.portfolio.product.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ProductWritePlatformService {

	CommandProcessingResult createNewProduct(JsonCommand command);

	CommandProcessingResult updateProduct(JsonCommand command, Long entityId);

	CommandProcessingResult deleteProduct(Long entityId);

}
