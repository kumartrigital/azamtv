package org.mifosplatform.crm.ticketmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfficeTicketDetailsRepository extends
JpaRepository<OfficeTicketDetail, Long>,
JpaSpecificationExecutor<OfficeTicketDetail>{

}
