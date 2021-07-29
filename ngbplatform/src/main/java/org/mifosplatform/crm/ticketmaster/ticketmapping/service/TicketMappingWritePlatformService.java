package org.mifosplatform.crm.ticketmaster.ticketmapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TicketMappingWritePlatformService {

	CommandProcessingResult createTicketMapping(JsonCommand command);

	CommandProcessingResult updateTicketmapping(JsonCommand command, Long entityId);

	CommandProcessingResult deleteTicketmapping(Long entityId);

}
