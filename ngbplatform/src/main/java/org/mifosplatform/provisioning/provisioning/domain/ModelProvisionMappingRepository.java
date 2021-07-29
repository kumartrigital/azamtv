package org.mifosplatform.provisioning.provisioning.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModelProvisionMappingRepository  extends JpaRepository<ModelProvisionMapping,Long >,JpaSpecificationExecutor<ModelProvisionMapping>{
	
	@Query("from ModelProvisionMapping mp where mp.model =:model and mp.deleted = 'N'")
	ModelProvisionMapping findOneByModel(@Param("model") String model);

}


