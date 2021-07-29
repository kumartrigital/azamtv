package org.mifosplatform.collectionbatch.ratableusagemetric.service;

import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface RatableUsageMetricReadPlatformService {
	
	Page<RatableUsageMetricData> retriveAllRatableUsageMetric(SearchSqlQuery searchRatableUsageMetric);

	RatableUsageMetricData retrieveRatableUsageMetric(Long id);

	List<RatableUsageMetricData> retrieveTemplateForDropdown();

	List<RatableUsageMetricData> retrieveRumForDropdown();

	

	

}
