package org.mifosplatform.portfolio.clientservice.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ClientServiceNotFoundException  extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientServiceNotFoundException(final Long clientServiceId) {
        super("error.msg.client.service.id.invalid", "Client Service with identifier " + clientServiceId + " does not exist", clientServiceId);
    }
    
}
