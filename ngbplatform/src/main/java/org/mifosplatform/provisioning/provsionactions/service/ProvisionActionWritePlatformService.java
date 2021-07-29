package org.mifosplatform.provisioning.provsionactions.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ProvisionActionWritePlatformService {

	CommandProcessingResult updateProvisionActionStatus(JsonCommand command);

}
