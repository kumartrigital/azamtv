package org.mifosplatform.portfolio.plan.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VolumeDetailsRepository  extends
JpaRepository<VolumeDetails, Long>,
JpaSpecificationExecutor<VolumeDetails>{

	@Query("from VolumeDetails volumeDetails where volumeDetails.planId =:planId")
	VolumeDetails findoneByPlanId(@Param("planId")Long planId);

}
