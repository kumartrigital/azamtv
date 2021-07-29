package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class MobileNumberLengthException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MobileNumberLengthException(String mobile) {
		super("error.msg.mobile.length.lessthan.10", "mobile number length should not be less than or greated than 10",mobile);
	}

}
