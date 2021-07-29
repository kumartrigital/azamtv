package org.mifosplatform.organisation.feemaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeeDetailRepository extends JpaRepository<FeeDetail, Long>,JpaSpecificationExecutor<FeeDetail>{

}
