package org.mifosplatform.collectionbatch.usagerateplan.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsageplanRepository extends JpaRepository<RatePlan, Long>, JpaSpecificationExecutor<RatePlan>{

}
