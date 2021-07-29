package org.mifosplatform.portfolio.plan.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RechargeFailedException extends AbstractPlatformResourceNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RechargeFailedException() {
		super("error.msg.Redemption.Failed", "Redemption Failed");
	}

}
