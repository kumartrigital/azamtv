package org.mifosplatform.finance.paymentsgateway.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PaymentGatewayConfigurationWritePlatformService {

	CommandProcessingResult updatePaymentGatewayConfig(Long configId, JsonCommand command);

	CommandProcessingResult createPaymentGatewayConfig(JsonCommand command);
	
}
