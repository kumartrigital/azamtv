/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.message.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class EmailNotFoundException extends AbstractPlatformResourceNotFoundException {

	/**
	 * 
	 * @param clientid
	 * 			id of the Client
	 */
    public EmailNotFoundException(final Long clientid) {
        super("error.msg.email.not.found", "User with this id" + clientid + "not exist", clientid);
        
    }

   
}
