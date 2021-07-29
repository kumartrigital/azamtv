package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ClientAlreadyCreatedException extends AbstractPlatformDomainRuleException {

	public ClientAlreadyCreatedException() {
		super("error.msg.billing.client.already.created", "client already Created.");
	}

}

