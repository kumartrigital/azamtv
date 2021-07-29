package org.mifosplatform.Revpay.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RevPayOrderRepository extends JpaRepository<RevpayOrder, Long> {

	
	@Query("from RevpayOrder order where order.txId=:txid")
	RevpayOrder findOneByTxid(@Param("txid") String txid);

}

