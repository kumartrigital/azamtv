package org.mifosplatform.organisation.partneragreement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface AgreementRepository extends JpaRepository<Agreement, Long>,
JpaSpecificationExecutor<Agreement>  {

}
