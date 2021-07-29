package org.mifosplatform.crm.clientprospect.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.data.ClientProspectData;
import org.mifosplatform.crm.clientprospect.data.ProspectDetailAssignedToData;
import org.mifosplatform.crm.clientprospect.data.ProspectDetailData;
import org.mifosplatform.crm.clientprospect.data.ProspectPlanCodeData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface ClientProspectReadPlatformService {

	public Collection<ClientProspectData> retriveClientProspect();

	public ProspectDetailData retriveClientProspect(Long clientProspectId);

	public Collection<ProspectPlanCodeData> retrivePlans();

	public List<ProspectDetailAssignedToData> retrieveUsers();

	public List<ProspectDetailData> retriveProspectDetailHistory(Long prospectdetailid, Long userId);

	public ClientProspectData retriveSingleClient(Long id, Long userId);

	public Page<ClientProspectData> retriveClientProspect(SearchSqlQuery searchClientProspect, Long userId);

	ClientProspectData retriveLeadForEventAction(Long id);

	ClientProspectData retriveLeadForEventActionFollowUp(Long id);

	public ClientProspectData retriveLeadForEventActionCreateLead(Long clientId);
}
