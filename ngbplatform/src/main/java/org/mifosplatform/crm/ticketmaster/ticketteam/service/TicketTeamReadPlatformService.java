package org.mifosplatform.crm.ticketmaster.ticketteam.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.ticketteam.data.TicketTeamData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TicketTeamReadPlatformService {

	TicketTeamData retrieveTicketTeam(Long ticketteamId);
	Page<TicketTeamData> retrieveTicketTeam(SearchSqlQuery searchTicketTeam);

	

}
