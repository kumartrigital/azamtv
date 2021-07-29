package org.mifosplatform.portfolio.property.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PropertyCodeAllocatedException extends AbstractPlatformDomainRuleException {

 
	private static final long serialVersionUID = 1L;

	public PropertyCodeAllocatedException(String msg) {
        super("error.msg.property.code.already.allocated", " property Code is already allocated to client", msg);
    }
    
    public PropertyCodeAllocatedException() {
        super("error.msg.property.code.already.allocated.or.invalid.code", " property Code is already allocated to client", "");
    }

	
}
