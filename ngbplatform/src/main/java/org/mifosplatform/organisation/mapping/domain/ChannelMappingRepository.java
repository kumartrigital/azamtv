package org.mifosplatform.organisation.mapping.domain;


import java.util.Set;

import org.mifosplatform.crm.ticketmaster.ticketmapping.domain.TicketTeamMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelMappingRepository  extends JpaRepository<ChannelMapping, Long>,
JpaSpecificationExecutor<ChannelMapping>{
	
	
	@Query("from ChannelMapping m where m.productId =:productId and m.isDeleted='N'")
	Set<ChannelMapping> findByChannelIdValue(@Param("productId") Long productId);

}
