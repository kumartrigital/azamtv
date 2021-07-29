package org.mifosplatform.crm.ticketmaster.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.crm.ticketmaster.data.SubCategoryData;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.data.UsersData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TicketMasterReadPlatformService {

	List<UsersData> retrieveUsers();

	List<TicketMasterData> retrieveClientTicketDetails(Long clientId);

	TicketMasterData retrieveSingleTicketDetails(Long clientId, Long ticketId);

	List<EnumOptionData> retrievePriorityData();

	List<TicketMasterData> retrieveClientTicketHistory(Long ticketId);

	TicketMasterData retrieveTicket(Long clientId, Long ticketId);
	
	Page<ClientTicketData> retrieveAssignedTicketsForNewClient(SearchSqlQuery searchTicketMaster, String statusType, String fromDate, String toDate,String type);

	List<SubCategoryData> retrieveSubCategory(final int codeValue);

	
	List<TicketMasterData> retrieveOfficeTicketDetails(Long officeId);

	TicketMasterData retrieveSingleOfficeTicketDetails(Long officeId,	Long ticketId);

	List<TicketMasterData> retrieveTicketsDetails(Long ticketId);

	List<TicketMasterData> retrieveTicketDetailsByOfficeId(Long officeId);

	List<TicketMasterData> retrieveAllOfficeTicketDetails();
	

	
	
}