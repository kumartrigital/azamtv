package org.mifosplatform.organisation.voucher.exception;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class OfficeBalanceIsNotEnoughException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constructor with Default Message
	 * @param balance */
	public OfficeBalanceIsNotEnoughException(BigDecimal balance) {
		super("error.msg.itemmove.office.balance.not enough.exception", " Requested itemsale failed because office in not have enough balance ", balance);
		// TODO Auto-generated constructor stub
	}
	  

}
