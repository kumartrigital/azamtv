package org.mifosplatform.finance.officebalance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfficeBalanceRepository extends JpaRepository<OfficeBalance, Long>,
		JpaSpecificationExecutor<OfficeBalance> {

	@Query("from OfficeBalance officeBalance where officeBalance.officeId =:officeId")
	OfficeBalance findOneByOfficeId(@Param("officeId") final Long officeId);

}
