package org.mifosplatform.portfolio.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>,
JpaSpecificationExecutor<Product>{
	
	@Query("from Product product where product.productCode =:productCode")
	Product findOneByProductId(@Param("productCode") String productCode);

	@Query("from Product product where product.productPoid =:productPoid")
	Product findOneByProductPoid(@Param("productPoid") Long productPoid);
}
