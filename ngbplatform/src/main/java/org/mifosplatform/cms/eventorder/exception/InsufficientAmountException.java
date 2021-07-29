package org.mifosplatform.cms.eventorder.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InsufficientAmountException extends AbstractPlatformDomainRuleException {

    public InsufficientAmountException(String msg) {
        super("error.msg.insufficient.amount.for."+msg, "Insufficient Amount to Book The Order");
    }
    
    public InsufficientAmountException(Long clientId) {
        super("error.msg.insufficient.balance.", "Insufficient Amount to Book The Order");
    }
   
}
