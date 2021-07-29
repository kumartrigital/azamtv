package org.mifosplatform.collectionbatch.unitofmeasurement.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface UnitOfmeasurementWritePlatformService {

	CommandProcessingResult createUnitOfmeasurement(JsonCommand command);

	CommandProcessingResult updateUnitOfmeasurement(JsonCommand command, Long entityId);

}
