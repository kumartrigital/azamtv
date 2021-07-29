package org.mifosplatform.provisioning.networkelement.service;

import java.util.List;

import org.mifosplatform.celcom.domain.PaymentTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElement;
import org.mifosplatform.provisioning.networkelement.domain.StatusTypeEnum;

public interface NetworkElementReadPlatformService {
	
	
NetworkElementData retrieveNetworkElement(Long networkelementId);
Page<NetworkElementData> retrieveNetworkElement(SearchSqlQuery searchNetworkElement);
List<StatusTypeEnum> retrieveStatusTypeEnum();
List<NetworkElementData> retrieveNetworkElements();
List<NetworkElementData> retriveNetworkElementsForService(String query);
	
}
	


