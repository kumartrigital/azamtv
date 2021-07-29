package org.mifosplatform.organisation.smartsearch.service;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.smartsearch.data.AdvanceSearchData;
import org.mifosplatform.portfolio.group.service.SearchParameters;

public interface AdvanceSearchReadPlafformService {

	Page<AdvanceSearchData> retrieveAllSearchData(SearchParameters searchParameters);

}
