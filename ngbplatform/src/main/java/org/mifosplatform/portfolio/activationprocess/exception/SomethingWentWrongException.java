package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SomethingWentWrongException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SomethingWentWrongException(String message) {
		super("error.msg.message", "something went wrong please contact support team",message);
	}

}
