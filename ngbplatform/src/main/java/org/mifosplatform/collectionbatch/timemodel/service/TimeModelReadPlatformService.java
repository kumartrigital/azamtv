package org.mifosplatform.collectionbatch.timemodel.service;

import java.util.List;

import org.mifosplatform.collectionbatch.timemodel.data.TimeModelData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TimeModelReadPlatformService {

	List<TimeModelData> retrieveTimeModelForDropdown();

	List<TimeModelData> retrieveTimePeriodForDropdown();

	Page<TimeModelData> retrieveAllTimemodels(SearchSqlQuery searchTimemodels);

	


}
