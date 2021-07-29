package org.mifosplatform.logistics.agent.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ItemSaleRepository extends JpaRepository<ItemSale, Long>{
	
	@Query(value ="select office_id from m_appuser where username=:username and is_deleted!=1", nativeQuery = true)
	Long getOffice(@Param("username") String username);
	
	
	@Transactional
	@Modifying
	@Query(value ="update b_itemsale set received_quantity =:quantity , status =:status where item_id =:itemSaleId", nativeQuery = true)
	void saveDetails(@Param("quantity") Long quantity , @Param("status") String status , @Param("itemSaleId") Long itemSaleId);

}
