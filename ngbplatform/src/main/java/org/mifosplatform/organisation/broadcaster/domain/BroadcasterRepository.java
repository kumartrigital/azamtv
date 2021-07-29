package org.mifosplatform.organisation.broadcaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BroadcasterRepository extends JpaRepository<Broadcaster, Long>,
JpaSpecificationExecutor<Broadcaster> {

}
