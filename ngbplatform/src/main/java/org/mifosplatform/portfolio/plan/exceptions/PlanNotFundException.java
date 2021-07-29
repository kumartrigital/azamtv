package org.mifosplatform.portfolio.plan.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PlanNotFundException extends AbstractPlatformResourceNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlanNotFundException() {
		super("error.msg.depositproduct.id.invalid","Charge Code already exists with same plan");
	}

public PlanNotFundException(Long planId) {
	super("error.msg.plan.with.id.not.exists","Plan was alreay deleted");

}
public PlanNotFundException(String planCode) {
	super("error.msg.plan.with.code.not.exists","Plan" +planCode+ "doesn't exist");
}

}
