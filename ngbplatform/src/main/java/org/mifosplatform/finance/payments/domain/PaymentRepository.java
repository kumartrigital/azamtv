package org.mifosplatform.finance.payments.domain;

import java.util.List;

import org.mifosplatform.portfolio.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
JpaSpecificationExecutor<Payment> {

	
}
