package org.mifosplatform.portfolio.clientservice.domain;

import java.util.List;

import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author trigital
 *
 */
public interface ClientServiceRepository extends JpaRepository<ClientService,Long >,JpaSpecificationExecutor<ClientService>{

    @Query("from ClientService clientService where clientService.clientServicePoid=:clientServicePoid")
	ClientService findClientServiceByPoId(@Param("clientServicePoid") String clientServicePoid);
    
	@Query("from ClientService clientService where clientService.clientId=:clientId and clientService.status='ACTIVE'")
	List<ClientService> findwithClientId(@Param("clientId") Long clientId);
	
	@Query("from ClientService clientService where clientService.clientId=:clientId and clientService.status='SUSPENDED'")
	List<ClientService> findwithClientIdSuspend(@Param("clientId") Long clientId);
	
	@Query("from ClientService clientService where clientService.clientId=:clientId and clientService.status!='TERMINATED'")
	List<ClientService> findwithClientId1(@Param("clientId") Long clientId);
	
	/*
	 * @Query("from ClientService clientService where clientService.clientId=:clientId and clientService.status='ACTIVE'"
	 * ) ClientService findClientServicewithClientId(@Param("clientId") Long
	 * clientId);
	 * 
	 */
	
	@Query("from ClientService clientService where clientService.clientId=:clientId")
	List<ClientService> findClientServicewithClientIds(@Param("clientId") Long clientId);

	@Query("from ClientService clientService where clientService.clientId=:clientId")
	ClientService findClientServicewithClientId(@Param("clientId") Long clientId);

	
}

