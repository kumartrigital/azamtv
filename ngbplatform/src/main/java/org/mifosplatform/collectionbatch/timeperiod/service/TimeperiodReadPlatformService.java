package org.mifosplatform.collectionbatch.timeperiod.service;

import org.mifosplatform.collectionbatch.timeperiod.data.TimePeriodNewData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TimeperiodReadPlatformService {

	TimePeriodNewData retriveTimeperiods(Long timeperiodId);

	Page<TimePeriodNewData> retrieveTimePeriodData(SearchSqlQuery searchTimePeriodNewData);

}
