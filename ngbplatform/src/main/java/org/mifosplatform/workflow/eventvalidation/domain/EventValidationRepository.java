package org.mifosplatform.workflow.eventvalidation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventValidationRepository extends
		JpaRepository<EventValidation, Long>,
		JpaSpecificationExecutor<EventValidation> {
	
	@Query("from EventValidation validation where validation.eventName =:eventName ")
	EventValidation findOneByEventName(@Param("eventName")final String eventName);

}
