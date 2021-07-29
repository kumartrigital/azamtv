package org.mifosplatform.portfolio.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientBillProfileInfoRepository extends JpaRepository<ClientBillProfileInfo, Long>, JpaSpecificationExecutor<ClientBillProfileInfo>{

	@Query("from ClientBillProfileInfo clientBillProfileInfo where clientId.clientId=:clientId")
	ClientBillProfileInfo findwithclientId(@Param("clientId") Long clientId);
	
	
}
