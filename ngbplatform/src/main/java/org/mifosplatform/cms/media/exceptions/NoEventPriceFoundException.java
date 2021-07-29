package org.mifosplatform.cms.media.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoEventPriceFoundException extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoEventPriceFoundException() {
        super("error.msg.event.price.not.found", "No event price found");
    }
    
   
}
