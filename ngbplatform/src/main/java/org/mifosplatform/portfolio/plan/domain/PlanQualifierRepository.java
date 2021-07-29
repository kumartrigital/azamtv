package org.mifosplatform.portfolio.plan.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlanQualifierRepository  extends JpaRepository<PlanQualifier, Long>,
JpaSpecificationExecutor<Long>{

	List<PlanQualifier> findOneByPlanId(Long planId);

}
