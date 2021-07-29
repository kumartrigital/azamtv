package org.mifosplatform.billing.linkup.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface LinkupAccountWritePlatformService {

	public CommandProcessingResult createLinkupAccount(JsonCommand command);

}

