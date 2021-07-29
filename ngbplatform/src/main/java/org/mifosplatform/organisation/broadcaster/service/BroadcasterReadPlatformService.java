package org.mifosplatform.organisation.broadcaster.service;

import java.util.List;


import org.mifosplatform.celcom.domain.PlanTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;

public interface BroadcasterReadPlatformService {
	

	Page<BroadcasterData> retrieveBroadcaster(SearchSqlQuery searchBroadcaster);

	BroadcasterData retrieveBroadcaster(Long broadcasterId);
	
	List<BroadcasterData> retrieveBroadcastersForDropdown();

	
}
