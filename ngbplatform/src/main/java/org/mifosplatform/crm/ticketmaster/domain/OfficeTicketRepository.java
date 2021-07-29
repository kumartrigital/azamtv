package org.mifosplatform.crm.ticketmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfficeTicketRepository extends
JpaRepository<OfficeTicket, Long>,
JpaSpecificationExecutor<OfficeTicket>{

}
