package org.mifosplatform.collectionbatch.unitofmeasurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UnitOfmeasurementRepository extends JpaRepository<UnitOfmeasurement, Long>,
JpaSpecificationExecutor<UnitOfmeasurement> {

}
