package org.mifosplatform.finance.paymentsgateway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentGatewayConfigurationRepository extends JpaRepository<PaymentGatewayConfiguration, Long>,
    JpaSpecificationExecutor<PaymentGatewayConfiguration> {
	
	PaymentGatewayConfiguration findOneByName(String name);

}
