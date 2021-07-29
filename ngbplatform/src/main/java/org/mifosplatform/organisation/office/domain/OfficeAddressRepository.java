package org.mifosplatform.organisation.office.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfficeAddressRepository extends JpaRepository<OfficeAddress, Long>,
		JpaSpecificationExecutor<OfficeAddress> {

	
	@Query("from OfficeAddress address where address.office =:office")
	OfficeAddress findOneWithPartnerId(@Param("office") Office office);
	
}


