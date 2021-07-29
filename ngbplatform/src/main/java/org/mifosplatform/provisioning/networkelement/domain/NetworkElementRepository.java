package org.mifosplatform.provisioning.networkelement.domain;

import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NetworkElementRepository extends JpaRepository<NetworkElement, Long>,
JpaSpecificationExecutor<NetworkElement>{

	@Query("from NetworkElement networkElement where networkElement.systemcode=:systemcode")
	NetworkElement findwithCode(@Param("systemcode") String systemcode);
}
