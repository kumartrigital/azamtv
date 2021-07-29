package org.mifosplatform.billing.selfcare.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SelfCareTemporaryAlreadyExistException extends AbstractPlatformDomainRuleException{

	public SelfCareTemporaryAlreadyExistException(final String emailId){
		 super("error.msg.billing.payment.already.exist.found", "payment already exist with this " + emailId);
	}
}
