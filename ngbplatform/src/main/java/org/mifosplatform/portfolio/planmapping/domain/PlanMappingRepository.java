package org.mifosplatform.portfolio.planmapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanMappingRepository extends JpaRepository<PlanMapping, Long>,JpaSpecificationExecutor<PlanMapping> {
	
	@Query("from PlanMapping planMapping where planMapping.planId =:planId")
	PlanMapping findOneByPlanId(@Param("planId")Long planId);

}
