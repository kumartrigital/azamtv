package org.mifosplatform.billing.planprice.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {

	@Query("from Price price where price.planCode =:planId and price.productId =:productId and price.contractPeriod =:duration and price.chargeCode = :chargeCode and price.isDeleted='n'")
	List<Price> findOneByPlanAndService(@Param("planId") Long planId, @Param("productId") Long productId,
			@Param("duration") String duration, @Param("chargeCode") String chargeCode);

	@Query("from Price price where price.planCode =:planId and price.contractPeriod =:duration and price.isDeleted='n'")
	List<Price> findChargeCodeByPlanAndContract(@Param("planId") Long planId, @Param("duration") String duration);

	@Query("from Price price where price.planCode =:planId and price.productId =:productId and price.contractPeriod =:duration "
			+ "and price.chargeCode = :chargeCode and price.priceRegion =:priceRegion and price.chargeOwner = :chargeOwner and  price.isDeleted='n' ")
	Price findOneByPlanAndService(@Param("planId") Long planId, @Param("productId") Long productId,
			@Param("duration") String duration, @Param("chargeCode") String chargeCode,
			@Param("priceRegion") Long priceRegion, @Param("chargeOwner") String chargeOwner);

	@Query("from Price price where price.planCode =:planId and price.productId =:productId and price.isDeleted='n'")
	List<Price> findOneByPlanAndProduct(@Param("planId") Long planId, @Param("productId") Long productId);

	@Query("from Price price where price.planCode =:planId and price.productId =:productId and price.chargeOwner = :chargeOwner  and price.isDeleted='n'")
	List<Price> findOneByPlanAndChargeOwner(@Param("planId") Long planId, @Param("productId") Long productId,
			@Param("chargeOwner") String chargeOwner);

	@Query("from Price price where price.planCode =:planId and price.isDeleted = 'n'")
	List<Price> findplansByPlanID(@Param("planId") Long planId);
	
	

	@Query("from Price price where price.planCode =:planId and price.isDeleted = 'n' and chargeOwner = 'self'")
	Price findplansByPlanIdChargeOwnerSelf(@Param("planId") Long planId);
	
	@Query("from Price price where price.planCode =:planId and price.isDeleted = 'n' and chargeOwner = 'parent'")
	Price findplansByPlanIdChargeOwnerParent(@Param("planId") Long planId);
	

}
