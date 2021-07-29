package org.mifosplatform.collectionbatch.template.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TemplateWritePlatformService {

	CommandProcessingResult createTemplates(JsonCommand command);

	CommandProcessingResult updateTemplates(Long entityId, JsonCommand command);

}
