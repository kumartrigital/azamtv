package org.mifosplatform.portfolio.product.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.domain.ProductDetailData;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;

public interface ProductReadPlatformService {

	Page<ProductData> retrieveProduct(SearchSqlQuery searchProduct);

	ProductData retrieveProduct(Long productId);

	Collection<ProductDetailData> retrieveProductDetailsAgainestMasterIdandParamCategory(Long productId, String paramCategory);

	List<ServiceData> retriveProducts(String serviceCategory, String productCode);

	Collection<ProductDetailData> retrieveProductDetails(Long productId, String paramCategory);

	Long retrieveSession(String username);

	
}
