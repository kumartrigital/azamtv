package org.mifosplatform.portfolio.activationprocess.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaseDetailsRepository
		extends JpaRepository<LeaseDetails, Long>, JpaSpecificationExecutor<LeaseDetails> {

	@Query("from LeaseDetails lease where lease.mobileNumber=:mobileNO")
	LeaseDetails findLeaseDetailsByMobileNo(@Param("mobileNO") String mobileNO);

}
