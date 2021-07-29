package org.mifosplatform.portfolio.jvtransaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JVTransactionRepository extends JpaRepository<JVTransaction, Long>,
JpaSpecificationExecutor<JVTransaction> {

}
