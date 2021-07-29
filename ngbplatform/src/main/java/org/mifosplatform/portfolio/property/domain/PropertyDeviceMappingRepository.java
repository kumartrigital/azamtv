package org.mifosplatform.portfolio.property.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyDeviceMappingRepository extends JpaRepository<PropertyDeviceMapping, Long>,
JpaSpecificationExecutor<PropertyDeviceMapping>{

	@Query("from PropertyDeviceMapping propertyDeviceMapping where propertyDeviceMapping.serialNumber =:serialNumber and propertyDeviceMapping.isDeleted = 'N'")
	PropertyDeviceMapping findBySerailNumber(@Param("serialNumber")String serialNumber);
	
	@Query("from PropertyDeviceMapping propertyDeviceMapping where propertyDeviceMapping.propertyCode =:propertyCode and propertyDeviceMapping.isDeleted = 'N'")
	List<PropertyDeviceMapping> findByPropertyCode(@Param("propertyCode")String propertyCode);

	@Query("from PropertyDeviceMapping propertyDeviceMapping where propertyDeviceMapping.serialNumber =:serialNumber and propertyDeviceMapping.clientId =:clientId and propertyDeviceMapping.isDeleted = 'N'")
	PropertyDeviceMapping findByCustomerSerailNumber(@Param("serialNumber")String serialNumber, @Param("clientId")Long clientId);


}

