package org.mifosplatform.portfolio.clientdiscount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientDiscountRepository extends JpaRepository<ClientDiscount, Long>,
JpaSpecificationExecutor<ClientDiscount> {

}
