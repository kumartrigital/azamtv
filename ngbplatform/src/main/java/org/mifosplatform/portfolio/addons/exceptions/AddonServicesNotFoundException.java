package org.mifosplatform.portfolio.addons.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AddonServicesNotFoundException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddonServicesNotFoundException(final Long orderId) {
		super("error.msg.addons.not.found.with.this.identifier","Addons not found with this identifier",orderId);
		
	}

}
