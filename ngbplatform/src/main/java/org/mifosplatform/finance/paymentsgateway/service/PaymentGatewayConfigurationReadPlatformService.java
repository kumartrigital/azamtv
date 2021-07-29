package org.mifosplatform.finance.paymentsgateway.service;

import org.mifosplatform.infrastructure.configuration.data.ConfigurationData;
import org.mifosplatform.infrastructure.configuration.data.ConfigurationPropertyData;

public interface PaymentGatewayConfigurationReadPlatformService {
	
	ConfigurationData retrievePaymentGatewayConfiguration();

	ConfigurationPropertyData retrievePaymentGatewayConfiguration(Long configId);

}
