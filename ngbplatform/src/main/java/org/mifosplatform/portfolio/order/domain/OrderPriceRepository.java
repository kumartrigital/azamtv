package org.mifosplatform.portfolio.order.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderPriceRepository extends

JpaRepository<OrderPrice, Long>,
JpaSpecificationExecutor<OrderPrice>{
	
	@Query("from OrderPrice op where op.orders =:orderId and is_deleted='N'")
	OrderPrice findOrders(@Param("orderId") Order orderId);
	
	@Query("from OrderPrice op where op.orders =:orderId and is_deleted='N'")
	List<OrderPrice> findOrderList(@Param("orderId") Order orderId);
}
