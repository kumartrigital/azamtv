package org.mifosplatform.provisioning.provisioning.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProvisioningRequestRepository  extends JpaRepository<ProvisioningRequest, Long>, 
JpaSpecificationExecutor<ProvisioningRequest>{

	@Query("from ProvisioningRequest provisioningRequest where provisioningRequest.status = 'N'")
	List<ProvisioningRequest> findUnProcessedProvisioningRequests(); 
	
}
