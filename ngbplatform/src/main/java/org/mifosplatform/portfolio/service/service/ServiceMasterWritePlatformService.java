package org.mifosplatform.portfolio.service.service;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ServiceMasterWritePlatformService {

	 CommandProcessingResult updateService(Long serviceId, JsonCommand command);

	 CommandProcessingResult deleteService(Long serviceId);

	 CommandProcessingResult createNewService(JsonCommand command);

	CommandProcessingResult addServiceAvailability(JsonCommand command,
			Long addressId);

}
