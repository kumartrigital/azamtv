package org.mifosplatform.portfolio.property.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PropertyWriteplatformService {

	CommandProcessingResult createProperty(JsonCommand command);

	CommandProcessingResult deleteProperty(Long entityId);

	CommandProcessingResult updateProperty(Long entityId, JsonCommand command);

	CommandProcessingResult createServiceTransfer(Long entityId,JsonCommand command);

	CommandProcessingResult createPropertyMasters(JsonCommand command);

	CommandProcessingResult updatePropertyMaster(Long entityId,JsonCommand command);

	CommandProcessingResult deletePropertyMaster(Long entityId);

	CommandProcessingResult allocatePropertyDevice(Long entityId,JsonCommand command);

}
