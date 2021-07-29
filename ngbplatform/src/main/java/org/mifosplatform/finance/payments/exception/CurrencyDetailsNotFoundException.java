package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CurrencyDetailsNotFoundException extends AbstractPlatformDomainRuleException {


	private static final long serialVersionUID = -2726286660273906232L;
	public CurrencyDetailsNotFoundException(final String currencyCode) {
		super("error.msg.payments.currency.details.invalid", "currency Details Not Found"+currencyCode+". ");
	}
}
