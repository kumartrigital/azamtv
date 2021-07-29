package org.mifosplatform.collectionbatch.template.service;

import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.template.data.TemplateData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TemplateReadPlatformService {

	Page<TemplateData> retrieveTemplates(SearchSqlQuery searchTemplate);

	List<TemplateData> retrieveTemplate(Long templateId);

	List<RatableUsageMetricData> retrieveFieldNamesForDropdown();

	

}
