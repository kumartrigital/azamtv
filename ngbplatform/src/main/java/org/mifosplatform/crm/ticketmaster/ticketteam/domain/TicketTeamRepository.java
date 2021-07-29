package org.mifosplatform.crm.ticketmaster.ticketteam.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


	public interface TicketTeamRepository  extends JpaRepository<TicketTeam, Long>,
	JpaSpecificationExecutor<TicketTeam>{
		
}
