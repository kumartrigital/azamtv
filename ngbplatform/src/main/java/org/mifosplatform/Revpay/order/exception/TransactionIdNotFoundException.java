package org.mifosplatform.Revpay.order.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class TransactionIdNotFoundException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionIdNotFoundException(Long txid) {
		super("error.msg.itemdetails.serialnumber.not.found", "TransactionId  not Exist with this " + txid, txid);
	}

}
