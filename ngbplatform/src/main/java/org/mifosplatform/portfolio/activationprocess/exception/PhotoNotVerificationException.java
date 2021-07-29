package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PhotoNotVerificationException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PhotoNotVerificationException(String mobile) {
		super("error.msg.photo", "Photo not verified",mobile);
	}

}
