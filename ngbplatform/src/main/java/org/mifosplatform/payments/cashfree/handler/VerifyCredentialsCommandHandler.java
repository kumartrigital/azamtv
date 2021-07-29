package org.mifosplatform.payments.cashfree.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.voucher.service.VoucherWritePlatformService;
import org.mifosplatform.payments.cashfree.service.CashFreePaymentGatewayWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CREDENTIALS", action = "VERIFY")
public class VerifyCredentialsCommandHandler  implements NewCommandSourceHandler{

	
	private final CashFreePaymentGatewayWritePlatformService cashFreePaymentGatewayWritePlatformService;

    @Autowired
    public VerifyCredentialsCommandHandler(final CashFreePaymentGatewayWritePlatformService cashFreePaymentGatewayWritePlatformService) {
        this.cashFreePaymentGatewayWritePlatformService = cashFreePaymentGatewayWritePlatformService;
    }

    @Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
    	return this.cashFreePaymentGatewayWritePlatformService.VerifyCredentials();
	}
}
