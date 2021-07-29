package org.mifosplatform.collectionbatch.unitofmeasurement.service;

import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.unitofmeasurement.data.UnitOfmeasurementData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface UnitOfmeasurementReadPlatformService {

	Page<UnitOfmeasurementData> retriveAllUnitOfmeasurement(SearchSqlQuery searchUnitOfmeasurement);

	UnitOfmeasurementData retriveUnitOfmeasurement(Long id);

	List<UnitOfmeasurementData> retrieveUomForDropdown();

	


}
