package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ReceiptNoDuplicateException extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReceiptNoDuplicateException() {
        super("error.msg.billing.order.not.found", "ReceiptNo with this number is already exist ");
    }
    
    public ReceiptNoDuplicateException(final String msg) {
        super("error.msg.receipt.no.duplicate.exception", " ReceiptNo with this number is already exist ", msg);
    }

	
}
