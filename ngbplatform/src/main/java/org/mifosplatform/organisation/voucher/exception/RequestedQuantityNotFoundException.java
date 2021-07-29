package org.mifosplatform.organisation.voucher.exception;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class RequestedQuantityNotFoundException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constructor with Default Message*/
	public RequestedQuantityNotFoundException() {
		super("error.msg.itemmove.quantity.isnotfound.exception", " Requested order quantity not found in central stock point");
		// TODO Auto-generated constructor stub
	}
	  

}
