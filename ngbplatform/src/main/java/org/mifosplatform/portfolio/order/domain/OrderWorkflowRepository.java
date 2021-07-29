package org.mifosplatform.portfolio.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderWorkflowRepository extends JpaRepository<OrderWorkflow, Long>,
JpaSpecificationExecutor<OrderWorkflow>{

 }
