package org.mifosplatform.provisioning.provisioning.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.provisioning.provisioning.data.ModelProvisionMappingData;

public interface ModelProvisionMappingReadPlatformService {

	Page<ModelProvisionMappingData> retrieveAll(SearchSqlQuery searchModelProvisionMapping);

	ModelProvisionMappingData retrieveSingleModelProvisionMapping(Long modelProvisionMappingId);
	
	
	List<ModelProvisionMappingData> retrieveAllForDropDown();

}
