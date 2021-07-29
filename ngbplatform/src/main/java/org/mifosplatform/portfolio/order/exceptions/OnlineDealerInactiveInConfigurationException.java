package org.mifosplatform.portfolio.order.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class OnlineDealerInactiveInConfigurationException extends AbstractPlatformDomainRuleException {

	public OnlineDealerInactiveInConfigurationException() {
		super("error.msg.unable.to.generate.changegroup.request.because.order.is.not.active",
				"Online Dealer is Inactive In Configuration");

	}
}
