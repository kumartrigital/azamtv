package org.mifosplatform.portfolio.plan.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanDetailsRepository extends JpaRepository<PlanDetails, Long> {

	/*@Query("from PlanDetails planDetails where planDetails.plan_id =:planId and is_deleted='N'")
	PlanDetails findPlanDetailsUsingId(@Param("planId") Long planId);*/
	
	@Query("from PlanDetails PlanDetails where PlanDetails.dealPoid=:dealPoid")
	PlanDetails findwithPlanPoid1(@Param("dealPoid") Long dealPoid);
	
	
}
