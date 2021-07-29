package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class DalpayRequestFailureException extends AbstractPlatformDomainRuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DalpayRequestFailureException(final Long user){
		 super("error.msg.finance.payment.not.found", "Dalpay Response 'user1' value invalid, user1= " + user);
	}
}