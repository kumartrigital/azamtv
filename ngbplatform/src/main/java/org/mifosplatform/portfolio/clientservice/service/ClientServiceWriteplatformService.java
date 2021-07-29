/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;

public interface ClientServiceWriteplatformService {

    CommandProcessingResult createClientService(JsonCommand command);

	CommandProcessingResult createClientServiceActivation(Long entityId, JsonCommand command);

	CommandProcessingResult suspendClientService(Long clientServiceId, JsonCommand command);

	CommandProcessingResult reactiveClientService(Long entityId, JsonCommand command);

	CommandProcessingResult terminateClientService(Long entityId,
			JsonCommand command);

	CommandProcessingResult updateServiceparameter(JsonCommand command,
			Long entityId);
	
	
	void createProvisioningService(Long clientId, Long clientServiceId);

	void updateClientServiceStatus(Long clientServiceId);

	void createProvisioningForModifyService(Long clientId, Long clientServiceId, String password, String sub_action);

}
