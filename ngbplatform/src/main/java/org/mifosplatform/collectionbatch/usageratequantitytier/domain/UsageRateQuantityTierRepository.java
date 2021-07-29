package org.mifosplatform.collectionbatch.usageratequantitytier.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsageRateQuantityTierRepository extends JpaRepository<UsageRateQuantityTier, Long>,
JpaSpecificationExecutor<UsageRateQuantityTier> {

}
