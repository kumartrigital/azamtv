package org.mifosplatform.portfolio.service.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceAvailabilityRepository extends JpaRepository<ServiceAvailabilty, Long>,
		JpaSpecificationExecutor<ServiceAvailabilty> {
	
	@Query("from ServiceAvailabilty serviceAvailabilty where serviceAvailabilty.addressId =:addressId and serviceAvailabilty.serviceId =:serviceId and serviceAvailabilty.addressType =:addressType")
	ServiceAvailabilty findOneByAddressIdAndServiceCode(@Param("addressId") Long addressId,@Param("serviceId") Long serviceId,@Param("addressType") String addressType);

}
