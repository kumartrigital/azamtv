package org.mifosplatform.collectionbatch.usageratebalance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsagebalanceRepository
		extends JpaRepository<UsageBalance, Long>, JpaSpecificationExecutor<UsageBalance> {

}
