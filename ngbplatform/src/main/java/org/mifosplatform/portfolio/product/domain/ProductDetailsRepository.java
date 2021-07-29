package org.mifosplatform.portfolio.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long>, JpaSpecificationExecutor<ProductDetails>{

}
