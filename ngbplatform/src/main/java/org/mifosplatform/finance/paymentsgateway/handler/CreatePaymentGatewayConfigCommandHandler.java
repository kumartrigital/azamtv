package org.mifosplatform.finance.paymentsgateway.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayConfigurationWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ashokreddy
 * 
 */
@Service
public class CreatePaymentGatewayConfigCommandHandler implements NewCommandSourceHandler{

	private final PaymentGatewayConfigurationWritePlatformService paymentGatewayWritePlatformService;

	@Autowired
	public CreatePaymentGatewayConfigCommandHandler(
			final PaymentGatewayConfigurationWritePlatformService paymentGatewayWritePlatformService) {
		this.paymentGatewayWritePlatformService = paymentGatewayWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.paymentGatewayWritePlatformService.createPaymentGatewayConfig(command);
	}
}
