package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class BvnNotVerificationException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BvnNotVerificationException(String mobile) {
		super("error.msg.Bvn", "Bvn not verified",mobile);
	}

}
