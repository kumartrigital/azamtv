package org.mifosplatform.logistics.mrn.domain;

import org.mifosplatform.logistics.mrn.domain.MRNDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MRNDetailsJpaRepository extends JpaRepository<MRNDetails, Long>,
		JpaSpecificationExecutor<MRNDetails> {

}
