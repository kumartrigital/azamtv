package org.mifosplatform.billing.discountmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DiscountMasterRepository extends JpaRepository<DiscountMaster, Long>, JpaSpecificationExecutor<DiscountMaster> {

	/*public final static String FIND_BY_CUSTOMER_CATEGORY="from DiscountMaster dm INNER JOIN dm.discountDetails dd where dm.id=:discountId and dd.categoryType=:categoryType";
	
	@Query(FIND_BY_CUSTOMER_CATEGORY)
	DiscountMaster findOneByCategory(@Param("discountId") Long discountId,@Param("categoryType") Long categoryType);
*/
}
