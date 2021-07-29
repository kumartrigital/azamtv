package org.mifosplatform.crm.ticketmaster.ticketmapping.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.useradministration.data.AppUserData;

public interface TicketMappingReadPlatformService {

	List<TicketTeamMappingData> retrieveTicketMapping(Long ticketmappingId);

	Page<TicketTeamMappingData> retrieveTicketMapping(SearchSqlQuery searchTicketMapping);

	List<TicketTeamMappingData> retrieveTicketTeamForDropdown();

	List<TicketTeamMappingData> retrieveSelectedUsers(Long teamId);

	List<AppUserData> retrieveAppUserDataForDropdown();


	
	
	//List<TicketTeamMappingData> retrieveTicketMappingData(Long ticketmappingId);

	

}
