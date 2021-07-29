package org.mifosplatform.billing.servicetransfer.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoFeeMasterRegionalPriceFound  extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoFeeMasterRegionalPriceFound() {
        super("error.msg.feemaster.regional.price.not.found", " No Regional Price Available for this Fee Master");
    }
}
    