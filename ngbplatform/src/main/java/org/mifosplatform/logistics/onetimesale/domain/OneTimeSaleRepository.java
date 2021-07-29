package org.mifosplatform.logistics.onetimesale.domain;

import java.util.List;

import org.mifosplatform.portfolio.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OneTimeSaleRepository extends
		JpaRepository<OneTimeSale, Long>, JpaSpecificationExecutor<OneTimeSale> {
	
	/*@Query("from OneTimeSale oneTimeSale where oneTimeSale.id=:id AND oneTimeSale.itemId=:itemId  ")
	OneTimeSale findOneTimeSaleByClientIdAndItemMasterId(@Param("id")Long id,@Param("itemId")Long itemId); 
*/
    /*@Query("from Order order where order.clientId=:clientId")
    Order findOrderByClientId(@Param("clientId")Long clientId);*/

}
