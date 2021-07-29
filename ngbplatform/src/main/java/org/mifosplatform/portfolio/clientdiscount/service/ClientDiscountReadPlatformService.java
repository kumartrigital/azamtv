package org.mifosplatform.portfolio.clientdiscount.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.clientdiscount.data.ClientDiscountData;

public interface ClientDiscountReadPlatformService {

	Page<ClientDiscountData> retrieveClientDiscount(SearchSqlQuery searchClientDiscount, Long clientId);

	ClientDiscountData retrieveClientDiscount(Long clientDiscountId);

}
