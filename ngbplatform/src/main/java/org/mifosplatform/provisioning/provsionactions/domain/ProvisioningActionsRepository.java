package org.mifosplatform.provisioning.provsionactions.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProvisioningActionsRepository extends JpaRepository<ProvisionActions, Long>,
                           JpaSpecificationExecutor<ProvisionActions>{
	
	
	@Query("from ProvisionActions provisionActions where provisionActions.provisiongType =:provEvent")
	ProvisionActions findOneByProvisionType(@Param("provEvent")String provEvent);

}
