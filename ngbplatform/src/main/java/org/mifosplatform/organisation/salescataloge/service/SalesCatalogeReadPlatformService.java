package org.mifosplatform.organisation.salescataloge.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public interface SalesCatalogeReadPlatformService {

	SalesCatalogeData retrieveSalesCataloge(Long salescatalogeId);

	Page<SalesCatalogeData> retrieveSalesCataloge(SearchSqlQuery searchSalesCataloge);

	List<SalesCatalogeData> retrieveSalesCatalogesForDropdown();

	List<PlanData> retrieveSelectedPlans(Long id);

}
