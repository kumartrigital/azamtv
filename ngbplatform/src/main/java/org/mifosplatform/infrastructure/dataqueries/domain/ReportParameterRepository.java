package org.mifosplatform.infrastructure.dataqueries.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportParameterRepository extends JpaRepository<ReportParameter, Long>, JpaSpecificationExecutor<ReportParameter> {

}
