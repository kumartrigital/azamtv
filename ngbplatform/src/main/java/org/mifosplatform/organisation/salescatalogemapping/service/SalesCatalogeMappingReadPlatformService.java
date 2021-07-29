package org.mifosplatform.organisation.salescatalogemapping.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.salescatalogemapping.data.SalesCatalogeMappingData;

public interface SalesCatalogeMappingReadPlatformService {


	Page<SalesCatalogeMappingData> retrieveSalesCatalogeMapping(SearchSqlQuery searchSalesCatalogeMapping);

	SalesCatalogeMappingData retrieveSalesCatalogeMapping(Long salescatalogemappingId);

	List<SalesCatalogeMappingData> retrieveSalesCatalogeMappingData();

}
