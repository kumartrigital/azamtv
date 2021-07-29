package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class UnableToCancelVoucherException extends AbstractPlatformDomainRuleException {

    public UnableToCancelVoucherException() {
        super("error.msg.unable.cancel.voucher.product.type", "unable to cancel voucher");
    }
    
   
   
}
