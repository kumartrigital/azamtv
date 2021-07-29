package org.mifosplatform.provisioning.entitlements.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CubiwareRequiredDataNotFoundException extends AbstractPlatformDomainRuleException {

	public CubiwareRequiredDataNotFoundException(Long clientId, String parameter) {
		super("error.msg.cubiware." + parameter + ".not.exist", parameter + " value does not exist and clientId=" + clientId, clientId);   
	}

}
