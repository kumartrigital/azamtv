package org.mifosplatform.organisation.feemaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeeMasterRepository extends JpaRepository<FeeMaster, Long>,JpaSpecificationExecutor<FeeMaster>{

}
