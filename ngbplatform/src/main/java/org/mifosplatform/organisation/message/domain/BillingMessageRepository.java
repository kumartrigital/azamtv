package org.mifosplatform.organisation.message.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 
 * @author ashokreddy
 *
 */
public interface BillingMessageRepository extends JpaRepository<BillingMessage, Long>,JpaSpecificationExecutor<BillingMessage>{

}
