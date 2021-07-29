package org.mifosplatform.logistics.itemdetails.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemDetailsAllocationRepository extends JpaRepository<ItemDetailsAllocation, Long>, JpaSpecificationExecutor<ItemDetailsAllocation>{

	@Query("from ItemDetailsAllocation allocations where allocations.clientId =:clientId and allocations.orderId =:orderId and allocations.status='allocated'")
	List<ItemDetailsAllocation> findRemainingAllocatedDevice(@Param("clientId") Long clientId,	@Param("orderId")Long orderId);
	
	@Query("from ItemDetailsAllocation allocations where allocations.clientId =:clientId and allocations.serialNumber =:serialNumber and allocations.isDeleted='N'")
	ItemDetailsAllocation findAllocatedDevicesBySerialNum(@Param("clientId") Long clientId,@Param("serialNumber")String serialNumber);
	
}
