package org.mifosplatform.collectionbatch.ratableusagemetric.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RatableUsageMetricRepository extends JpaRepository<RatableUsageMetric, Long>,
JpaSpecificationExecutor<RatableUsageMetric> {

}
