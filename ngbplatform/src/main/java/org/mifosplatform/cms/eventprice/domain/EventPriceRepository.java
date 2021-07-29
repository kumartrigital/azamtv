/**
 * 
 */
package org.mifosplatform.cms.eventprice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface for {@link EventPricing} Repository extends {@link JpaRepository}
 * and {@link JpaSpecificationExecutor}
 * 
 * @author pavani
 *
 */
public interface EventPriceRepository extends JpaRepository<EventPrice, Long>, JpaSpecificationExecutor<EventPrice> {

	@Query("from EventPrice eventPrice where eventPrice.eventId=:eventId")
	EventPrice findByEventID(@Param("eventId") Long long1);

}
