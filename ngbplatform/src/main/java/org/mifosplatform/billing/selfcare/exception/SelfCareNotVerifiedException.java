package org.mifosplatform.billing.selfcare.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SelfCareNotVerifiedException extends AbstractPlatformDomainRuleException {

	public SelfCareNotVerifiedException(final String emailId){
		 super("error.msg.billing.selfcare.not.verified", "Email Verification Not Verified with this " + emailId);
	}
	
}