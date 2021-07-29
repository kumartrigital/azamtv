package org.mifosplatform.portfolio.order.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderAddonsRepository  extends JpaRepository<OrderAddons, Long>,
   JpaSpecificationExecutor<OrderAddons>{

    @Query("from OrderAddons order where order.orderId =:orderId and order.status='ACTIVE'")
    List<OrderAddons> findAddonsByOrderId(@Param("orderId") final Long orderId);

    

	
}
