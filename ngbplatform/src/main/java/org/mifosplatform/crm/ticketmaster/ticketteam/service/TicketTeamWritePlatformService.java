package org.mifosplatform.crm.ticketmaster.ticketteam.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TicketTeamWritePlatformService {

	CommandProcessingResult createTicketTeam(JsonCommand command);
	CommandProcessingResult updateTicketteam(JsonCommand command, Long entityId);
	CommandProcessingResult deleteTicketteam(Long entityId);

}
