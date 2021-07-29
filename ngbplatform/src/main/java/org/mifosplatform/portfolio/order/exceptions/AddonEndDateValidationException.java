package org.mifosplatform.portfolio.order.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AddonEndDateValidationException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddonEndDateValidationException(final Long serviceId) {
		super("error.msg.addon.enddate.should.lessthan.order.enddate","Addon end date should lessthan order enddate",serviceId);
		
	}
	
	public AddonEndDateValidationException(final String serviceId) {
		super("error.msg.addon.provide.service.mapping.details","Provide service mapping details",serviceId);
		
	}

}
