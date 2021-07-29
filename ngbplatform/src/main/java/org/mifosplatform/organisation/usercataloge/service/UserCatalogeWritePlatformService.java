package org.mifosplatform.organisation.usercataloge.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface UserCatalogeWritePlatformService {

	CommandProcessingResult create(JsonCommand command);

	CommandProcessingResult updateUserCataloge(JsonCommand command, Long entityId);

	/*CommandProcessingResult deleteUserCataloge(Long entityId)*/;

}
