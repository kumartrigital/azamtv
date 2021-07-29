package org.mifosplatform.finance.clientbalance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientBalanceRepository extends JpaRepository<ClientBalance, Long>,
                         JpaSpecificationExecutor<ClientBalance>{
	
	@Query("from ClientBalance clientBalance where clientBalance.clientId =:clientId") 
	ClientBalance findByClientId(@Param("clientId") final Long clientId);
	
	@Query("from ClientBalance clientBalance where clientBalance.clientId =:clientId and  clientBalance.clientServiceId =:clientServiceId")
	ClientBalance findByClientAndClientServiceId(@Param("clientId")Long clientId,@Param("clientServiceId") Long clientServiceId);
    
	@Query("from ClientBalance clientBalance where clientBalance.clientId =:clientId and  clientBalance.clientServiceId =:clientServiceId and clientBalance.resourceId =:resourceId")
	ClientBalance findByClientAndClientServiceIdAndCurrencyId(@Param("clientId")Long clientId,@Param("clientServiceId") Long clientServiceId,@Param("resourceId") Long resourceId);
	
}
