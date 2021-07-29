package org.mifosplatform.organisation.address.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository  extends JpaRepository<Address, Long>,JpaSpecificationExecutor<Address>{

	//@Query("from Address address where address.clientId=:clientId and address.addressNo =:newPropertyCode and deleted='n'")
	//Address findOneByAddressNo(@Param("clientId") Long clientId, @Param("newPropertyCode") String newPropertyCode);
	
	
	@Query("from Address address where address.clientId=:clientId and address.addressKey ='PRIMARY' and deleted='n'")
	Address findOneByClientId(@Param("clientId") Long clientId);

	@Query("from Address address where address.clientId=:clientId and address.addressNo =:oldPropertyCode and deleted='n'")
	Address findOneByClientIdAndPropertyCode(@Param("clientId") Long clientId,@Param("oldPropertyCode") String oldPropertyCode);

	@Query("from Address address where address.addressNo=:addressNo and address.addressKey ='BILLING1' and deleted='n'")
	Address findOne(@Param("addressNo")String addressNo);
	
	
}
