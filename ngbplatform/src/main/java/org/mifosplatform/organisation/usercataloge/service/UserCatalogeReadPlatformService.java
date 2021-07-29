package org.mifosplatform.organisation.usercataloge.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.usercataloge.data.UserCatalogeData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public interface UserCatalogeReadPlatformService {

	Page<UserCatalogeData> retrieveUserCataloge(SearchSqlQuery searchUserCataloge);

	UserCatalogeData retrieveUserCataloge(Long usercatalogeId);

	List<UserCatalogeData> retrieveUserCatalogeOfUser(Long userId);

	List<SalesCatalogeData> retrieveSelectedSalesCataloges(Long userId);


	List<SalesCatalogeData> retrievePlans(Long userId, String category);
	
}
