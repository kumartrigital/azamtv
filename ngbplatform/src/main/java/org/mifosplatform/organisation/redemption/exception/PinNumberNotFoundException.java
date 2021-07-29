package org.mifosplatform.organisation.redemption.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PinNumberNotFoundException extends AbstractPlatformDomainRuleException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PinNumberNotFoundException(final String pinNumber) {
		super("error.msg.redemption.pinNumber.invalid", "Invalid Pin Number" + pinNumber , pinNumber);
    }
	
	

}
