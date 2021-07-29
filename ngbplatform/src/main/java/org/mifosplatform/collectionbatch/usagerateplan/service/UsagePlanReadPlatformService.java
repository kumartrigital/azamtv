package org.mifosplatform.collectionbatch.usagerateplan.service;

import org.mifosplatform.collectionbatch.usagerateplan.data.RatePlanData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface UsagePlanReadPlatformService {

	Page<RatePlanData> retriveAllRateplans(SearchSqlQuery searchRatablePlans);

}
