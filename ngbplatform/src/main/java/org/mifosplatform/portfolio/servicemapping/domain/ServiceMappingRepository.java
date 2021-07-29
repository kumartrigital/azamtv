package org.mifosplatform.portfolio.servicemapping.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceMappingRepository extends JpaRepository<ServiceMapping,Long>, 
JpaSpecificationExecutor<ServiceMapping>{

   @Query("from ServiceMapping serviceMapping where serviceMapping.serviceId =:serviceId")
	List<ServiceMapping> findOneByServiceId(@Param("serviceId") final Long serviceId);

}
