package org.mifosplatform.logistics.itemdetails.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemDetailsRepository extends JpaRepository<ItemDetails, Long> {

	@Query("from ItemDetails item where item.serialNumber = :macId")
	ItemDetails getInventoryItemDetailBySerialNum(@Param("macId") String macId);

	@Query("from ItemDetails item where item.cartoonNumber = :macId")
	List<ItemDetails> getInventoryItemDetailByCartonNum(@Param("macId") String macId);

	@Query("from ItemDetails item where item.serialNumber = :serialNo")
	ItemDetails getItemDetailBySerialNum(@Param("serialNo") String serialNo);

	@Query("from ItemDetails item where item.serialNumber = :serialNo and item.status = 'Available'")
	ItemDetails getItemDetailBySerial(@Param("serialNo") String serialNo);

	@Query("from ItemDetails item where item.clientId = :clientId")
	ItemDetails getItemDetailByClientId(@Param("clientId") Long clientId);

	/*
	 * @Query(value =
	 * "update b_item_detail id set id.office_id = :tooffice where id.office_id =:fromoffice and id.status ='Available' limit :quantity"
	 * , nativeQuery = true) void updateSerialNumbers(@Param("tooffice")Long
	 * tooffice,@Param("fromoffice") Long fromoffice, @Param("quantity")String
	 * quantity);
	 */
		
	@Query(value = "select * from b_item_detail item  where item.office_id = :fromoffice and item.status = 'Available' limit :quantity" ,nativeQuery = true)
	List<ItemDetails> getAvilableBoxes(@Param("fromoffice") Long fromoffice, @Param("quantity")Long quantity);

	
	@Query(value = "select * from b_item_detail item  where item.office_id = :fromoffice and item.status = 'Available' ORDER BY id DESC LIMIT 1" ,nativeQuery = true)
	ItemDetails getAvilableBox(@Param("fromoffice") Long fromoffice);

	
	
	@Query(value = "select * from b_item_detail item  where item.item_sale_id = :itemSaleId " ,nativeQuery = true)
	List<ItemDetails>  getItemsByItemSaleID(@Param("itemSaleId") Long itemSaleId);


}
