package org.mifosplatform.portfolio.service.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Long>,
JpaSpecificationExecutor<ServiceDetail>{

	
	@Query("from ServiceDetail serviceDetail where serviceDetail.serviceCode=:serviceCode")
	ServiceDetail findwithCode(@Param("serviceCode") String serviceCode);
}
