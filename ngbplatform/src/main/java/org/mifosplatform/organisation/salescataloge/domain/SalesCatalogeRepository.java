package org.mifosplatform.organisation.salescataloge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesCatalogeRepository extends JpaRepository<SalesCataloge, Long>,
JpaSpecificationExecutor<SalesCataloge> {
	
	@Query("from SalesCataloge salescataloge where salescataloge.id =:salescatalogeId and is_deleted='N'")
	SalesCataloge findSalesCatalogeCheckDeletedStatus(@Param("salescatalogeId") Long salescatalogeId);

}
