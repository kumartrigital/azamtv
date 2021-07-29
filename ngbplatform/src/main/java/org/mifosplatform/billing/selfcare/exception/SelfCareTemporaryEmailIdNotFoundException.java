package org.mifosplatform.billing.selfcare.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SelfCareTemporaryEmailIdNotFoundException extends AbstractPlatformDomainRuleException{

	public SelfCareTemporaryEmailIdNotFoundException(final String emailId){
		 super("error.msg.billing.userName.not.found", "userName/EmailId not found with this " + emailId);
	}
}
