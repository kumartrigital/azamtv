package org.mifosplatform.portfolio.plan.domain;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanRepository  extends JpaRepository<Plan, Long>,
JpaSpecificationExecutor<Plan>{

	@Query("from Plan plan where plan.id =:planId and is_deleted='N'")
	Plan findPlanCheckDeletedStatus(@Param("planId") Long planId);
	
	@Query("from Plan plan where plan.planCode=:planCode")
	Plan findwithName(@Param("planCode") String planCode);
	
	@Query("from Plan plan where plan.planPoid=:planPoid")
	Plan findwithPlanPoid(@Param("planPoid") Long planPoid);

	@Query("from Plan plan where plan.description=:description ")
	List<Plan> findwithPlanName(@Param("description") String description);
	
	@Query("from Plan plan where plan.planCode=:planCode and deleted='N'")
	Plan findwithPlanCode(@Param("planCode") String planCode);

	@Query(value = "select plan_code from b_plan_master where is_deleted='N'",nativeQuery = true)
	List<String> findAllPlanCode();
	

	

}
