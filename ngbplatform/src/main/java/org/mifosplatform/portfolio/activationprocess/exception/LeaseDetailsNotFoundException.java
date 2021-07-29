package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LeaseDetailsNotFoundException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LeaseDetailsNotFoundException(String mobile) {
		super("error.msg.NIN", "Details Not Found",mobile);
	}

}
