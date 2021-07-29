package org.mifosplatform.billing.selfcare.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentStatusAlreadyActivatedException extends AbstractPlatformDomainRuleException{

	public PaymentStatusAlreadyActivatedException(final String emailId){
		 super("error.msg.billing.selfcare.temp.paymentstatus.already.exist", "PaymentStatus Already Activated for this Email :" + emailId);
	}
}
