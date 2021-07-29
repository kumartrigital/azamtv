package org.mifosplatform.finance.paymentsgateway.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author ashokreddy
 * 
 */
@Service
public class OnlinePaymentGatewayCommandhandler implements NewCommandSourceHandler {

	private final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService;

	@Autowired
	public OnlinePaymentGatewayCommandhandler(
			final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService) {
		this.paymentGatewayWritePlatformService = paymentGatewayWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.paymentGatewayWritePlatformService.onlinePaymentGateway(command);
	}
}
