package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class MobileNumberDuplicationException extends AbstractPlatformDomainRuleException{
	
	private static final long serialVersionUID = 1L;

	public MobileNumberDuplicationException(String mobile) {
		super("error.msg.mobile.duplication", "mobile number duplication not allowed",mobile);
	}

}
