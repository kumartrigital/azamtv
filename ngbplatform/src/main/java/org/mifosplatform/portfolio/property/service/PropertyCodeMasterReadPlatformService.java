package org.mifosplatform.portfolio.property.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.property.data.PropertyCodeMasterData;

public interface PropertyCodeMasterReadPlatformService {
	
	Page<PropertyCodeMasterData> retrieveAllPropertyCodeMasterData(SearchSqlQuery searchPropertyDetails);

	List<PropertyCodeMasterData> retrievPropertyType(String propertyType);

}
