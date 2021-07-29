package org.mifosplatform.logistics.onetimesale.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemPairingRepository extends JpaRepository<ItemPairing, Long>, JpaSpecificationExecutor<ItemPairing> {
	
	@Query("from ItemPairing ip where ip.serialNo1 =:serialNo and is_deleted='N'")
	ItemPairing findOneBySerialNo(@Param("serialNo") String serialNo);
	
	@Query("from ItemPairing ip where ip.serialNo2 =:serialNo and is_deleted='N'")
	ItemPairing findOneBySerialNo2(@Param("serialNo") String serialNo);

}
