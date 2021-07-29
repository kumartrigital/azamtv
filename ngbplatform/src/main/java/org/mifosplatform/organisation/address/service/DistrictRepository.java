package org.mifosplatform.organisation.address.service;

import org.mifosplatform.organisation.address.domain.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DistrictRepository extends JpaRepository<District,Long>,JpaSpecificationExecutor<District>{

	

}
