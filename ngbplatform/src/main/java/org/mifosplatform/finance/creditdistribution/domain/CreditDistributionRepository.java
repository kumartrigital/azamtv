package org.mifosplatform.finance.creditdistribution.domain;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditDistributionRepository  extends JpaRepository<CreditDistribution, Long>,
JpaSpecificationExecutor<CreditDistribution>{
	
	@Query("from CreditDistribution creditDistribution where creditDistribution.paymentId =:paymentId ")
	List<CreditDistribution> findOneByPaymentId(@Param("paymentId")Long paymentId);

}
