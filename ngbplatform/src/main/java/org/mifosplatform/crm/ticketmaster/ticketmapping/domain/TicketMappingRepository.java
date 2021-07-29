package org.mifosplatform.crm.ticketmaster.ticketmapping.domain;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketMappingRepository extends JpaRepository<TicketTeamMapping, Long>,
JpaSpecificationExecutor<TicketTeamMapping> {

	@Query("from TicketTeamMapping ttm where ttm.teamId =:ticketmappingId and ttm.isDeleted='N'")
	Set<TicketTeamMapping> findTicketMappingId(@Param("ticketmappingId") Long ticketmappingId);

	
}
