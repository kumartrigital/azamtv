package org.mifosplatform.organisation.salescatalogemapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalesCatalogeMappingRepository extends JpaRepository<SalesCatalogeMapping, Long>,
JpaSpecificationExecutor<SalesCatalogeMapping>{

}
