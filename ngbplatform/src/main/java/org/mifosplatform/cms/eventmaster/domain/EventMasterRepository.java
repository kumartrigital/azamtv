package org.mifosplatform.cms.eventmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface for {@link EventMaster} Repository extends {@link JpaRepository}
 * and {@link JpaSpecificationExecutor}
 * 
 * @author pavani
 *
 */

public interface EventMasterRepository extends JpaRepository<EventMaster, Long>, JpaSpecificationExecutor<EventMaster> {

	@Query("from EventMaster eventMaster where eventMaster.eventDescription=:description")
	EventMaster findOneByDescription(@Param("description") String description);

	@Query("from EventMaster eventMaster where eventMaster.eventName=:eventName")
	EventMaster findOneByEventName(@Param("eventName") String eventName);

}
