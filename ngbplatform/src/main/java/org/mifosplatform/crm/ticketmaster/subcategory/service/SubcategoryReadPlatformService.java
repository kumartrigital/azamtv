package org.mifosplatform.crm.ticketmaster.subcategory.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.subcategory.data.SubcategoryDataT;
import org.mifosplatform.infrastructure.core.service.Page;


public interface SubcategoryReadPlatformService {
	
	SubcategoryDataT retrieveSubcategory(Long id);
	
	/*List<SubcategoryDataT> retrieveSubcategory(String subcategoryData);*/

	Page<SubcategoryDataT> retrieveSubcategory(SearchSqlQuery searchSubcategory);


}
