package org.mifosplatform.finance.chargeorder.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeTaxRepository extends JpaRepository<ChargeTax, Long>, JpaSpecificationExecutor<ChargeTax> {

}
