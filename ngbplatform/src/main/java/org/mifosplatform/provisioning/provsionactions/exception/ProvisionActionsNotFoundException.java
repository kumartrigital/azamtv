package org.mifosplatform.provisioning.provsionactions.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ProvisionActionsNotFoundException extends AbstractPlatformDomainRuleException {

    
	public ProvisionActionsNotFoundException() {
        super("error.msg.billing.provision.action.not.found", "provisioninga action is does not exist");
    }

	public ProvisionActionsNotFoundException(String provisionId) {
		
		 super("error.msg.billing.provision.action.not.found", "provisioninga action is does not exist",provisionId);
	}
}
