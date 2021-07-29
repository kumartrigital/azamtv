package org.mifosplatform.organisation.officepayments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentOfficeDetailsNotFoundException extends AbstractPlatformDomainRuleException
{
	private static final long serialVersionUID = -2726286660273906232L;
	public PaymentOfficeDetailsNotFoundException(final String paymentCode) {
		super("error.msg.payments.payment.details.invalid", "Payment Details Not Found"+paymentCode+". ",paymentCode);
	}
}
