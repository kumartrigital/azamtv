package org.mifosplatform.collectionbatch.timeperiod.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimeperiodRepository  extends JpaRepository<TimePeriodsNew, Long>, JpaSpecificationExecutor<TimePeriodsNew> {

}
