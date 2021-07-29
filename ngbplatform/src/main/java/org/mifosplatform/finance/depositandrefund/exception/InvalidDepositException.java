package org.mifosplatform.finance.depositandrefund.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidDepositException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidDepositException(final Long depositId) {
		super("error.msg.deposit.not.found.with.this.identifier","Deposit not found with this identifier",depositId);
		
	}

}
