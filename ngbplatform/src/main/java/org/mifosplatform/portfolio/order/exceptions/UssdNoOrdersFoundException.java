package org.mifosplatform.portfolio.order.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class UssdNoOrdersFoundException extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UssdNoOrdersFoundException() {
        super("error.msg.billing.order.not.found", "ORDER_ID_NOT_FOUND");
    }
 
}
