package org.mifosplatform.finance.paymentsgateway.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentGatewayConfigurationException extends AbstractPlatformDomainRuleException {

	public PaymentGatewayConfigurationException(String paymentGateway) {
		super("error.msg.paymentgateway.configure.details.not.found", paymentGateway + " PaymentGateway details are not Configured Properly","");
	}
}
