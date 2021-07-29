package org.mifosplatform.collectionbatch.timemodel.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimemodelRepository extends JpaRepository<TimeModel, Long>, JpaSpecificationExecutor<TimeModel>{

}
