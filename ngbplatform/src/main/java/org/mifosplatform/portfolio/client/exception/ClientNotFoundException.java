/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when client resources are not found.
 */
public class ClientNotFoundException extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientNotFoundException(final Long clientId) {
        super("error.msg.client.id.invalid", "Client with identifier " + clientId + " does not exist", clientId);
    }
    
    public ClientNotFoundException(final String email) {
        super("error.msg.not.exist", "Client with " + email + " does not exist", email);
    }
    
    public ClientNotFoundException(final String reason, final String deviceId) {
        super("error.msg.client.access.expection", reason, deviceId);
    }
    
    public ClientNotFoundException(final String accountNo,final Long clientId) {
        super("error.msg.client.not.found", "Client with account Number " + accountNo + " does not exist", accountNo);
    }
}