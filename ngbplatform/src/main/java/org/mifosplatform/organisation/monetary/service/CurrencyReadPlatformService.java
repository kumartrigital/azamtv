/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

public interface CurrencyReadPlatformService {

	Collection<CurrencyData> retrieveAllowedCurrencies();

	Collection<CurrencyData> retrieveAllPlatformCurrencies();
	
	Page<CurrencyData> retriveCurrencies(SearchSqlQuery searchCurrency);
	
	CurrencyData retriveCurrencies(Long currenciesId);
	
	Long findMaxofId(String currenyType);

	List<CurrencyData> retrieveCurrency();

	Collection<CurrencyData> retrieveAllPlatformnonCurrencies();

	

	

	
}