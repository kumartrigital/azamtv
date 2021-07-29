package org.mifosplatform.logistics.mrn.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidMrnIdException extends AbstractPlatformDomainRuleException {

 
    
    public InvalidMrnIdException(String mrnId) {
        super("error.msg.invalid.mrn.id", "Invalid Mrn Id", mrnId);
    }

}
