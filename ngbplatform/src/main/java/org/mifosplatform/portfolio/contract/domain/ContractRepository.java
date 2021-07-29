package org.mifosplatform.portfolio.contract.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContractRepository  extends JpaRepository<Contract, Long>,JpaSpecificationExecutor<Contract>{

	@Query("from Contract contract where contract.subscriptionPeriod =:contractPeriod")
	Contract findOneByContractId(@Param("contractPeriod") String contractPeriod);
	
	@Query("from Contract contract where contract.subscriptionPeriod=:subscriptionPeriod")
	Contract findwithName(@Param("subscriptionPeriod") String subscriptionPeriod);

}
