package org.mifosplatform.logistics.grv.domain;

import org.mifosplatform.logistics.mrn.domain.MRNDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GRVJpaRepository extends JpaRepository<GRVDetails, Long>,
JpaSpecificationExecutor<GRVDetails>{

}
