package org.mifosplatform.portfolio.service.domain;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceMasterRepository extends JpaRepository<ServiceMaster, Long>,
JpaSpecificationExecutor<ServiceMaster>{
	
	@Query("from ServiceMaster service where service.serviceCode =:serviceCode")
	ServiceMaster findOneByServiceCode(@Param("serviceCode") String serviceCode);
	
	@Query("from ServiceMaster serviceMaster  where serviceMaster.serviceType =:serviceType and is_deleted='N'")
	List<ServiceMaster> findServiceIdUsingserviceType(@Param("serviceType") String serviceType);


}
