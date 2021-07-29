package org.mifosplatform.portfolio.order.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository  extends JpaRepository<Order, Long>,
   JpaSpecificationExecutor<Order>{

    @Query("from Order order where order.id=(select max(newOrder.id) from Order newOrder where newOrder.orderNo =:orderNo and newOrder.status=3 )")
	Order findOldOrderByOrderNO(@Param("orderNo")String orderNo);

    @Query("from Order order where order.clientServiceId=:clientServiceId AND order.clientId=:clientId")
	List<Order> findOrdersByClientService(@Param("clientServiceId")Long clientServiceId,@Param("clientId")Long clientId); 

    @Query("from Order order where order.clientId=:clientId")
    Order findOrderByClientId(@Param("clientId")Long clientId);
    
    @Query("from Order order where order.clientServiceId=:clientServiceId")
	List<Order> findOrdersOnlyByClientService(@Param("clientServiceId")Long clientServiceId); 

    @Query("from Order order where order.orderNo=:orderNo")
   	Order findOrderByOrderNO(@Param("orderNo")String orderNo);
    
    @Query("from Order order where order.clientId=:clientId and planId=:leasePlanId and status=1")
    Order findOrderByClientIdWithLeasePlanActive(@Param("clientId")Long clientId, @Param("leasePlanId") Long leasePlanId);
    
    
}
