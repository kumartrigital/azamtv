package org.mifosplatform.portfolio.provisioncodemapping.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.provisioncodemapping.data.ProvisionCodeMappingData;


public interface ProvisionCodeMappingReadPlatformService {

	Page<ProvisionCodeMappingData> retrieveProvisionCodeMapping(SearchSqlQuery searchProvisionCodeMapping);

	ProvisionCodeMappingData retrieveProvisionCodeMapping(Long provisioncodemappingId);

	List<ProvisionCodeMappingData> retrieveProvisionCodeMappingsForDropdown();

}
