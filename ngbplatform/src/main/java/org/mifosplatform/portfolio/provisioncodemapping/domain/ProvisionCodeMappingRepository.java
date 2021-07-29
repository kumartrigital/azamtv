package org.mifosplatform.portfolio.provisioncodemapping.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProvisionCodeMappingRepository extends JpaRepository<ProvisionCodeMapping, Long>,
JpaSpecificationExecutor<ProvisionCodeMapping>{

}
