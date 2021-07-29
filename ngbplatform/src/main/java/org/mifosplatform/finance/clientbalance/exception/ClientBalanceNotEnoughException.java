package org.mifosplatform.finance.clientbalance.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ClientBalanceNotEnoughException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientBalanceNotEnoughException() {
		super("error.clinet.balance.not.enough exception","Client Balance Not Enough to Perform This Action");
	}
	
}
