package org.mifosplatform.organisation.groupsdetails.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface GroupsDetailsWritePlatformService {

	CommandProcessingResult createGroupsDetails(JsonCommand command);
	
	CommandProcessingResult createGroupsDetailsProvision(JsonCommand command);
	
	CommandProcessingResult generateStatment(JsonCommand command, Long entityId);
}
