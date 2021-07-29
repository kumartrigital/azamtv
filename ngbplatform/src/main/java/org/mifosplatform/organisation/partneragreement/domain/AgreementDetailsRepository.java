package org.mifosplatform.organisation.partneragreement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgreementDetailsRepository extends JpaRepository<AgreementDetails, Long>,
JpaSpecificationExecutor<AgreementDetails>  {

}
