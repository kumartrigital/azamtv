package org.mifosplatform.portfolio.addons.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddonsRepository  extends JpaRepository<AddonServices, Long>,
   JpaSpecificationExecutor<AddonServices>{

   

	
}
