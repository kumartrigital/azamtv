/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.billing.planprice.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class PriceNotFoundException extends AbstractPlatformResourceNotFoundException {

    public PriceNotFoundException(final String name) {
        super("error.msg.price.not.found", "price with id`" + name + "` does not exist", name);
    }

    public PriceNotFoundException(final Long codeId) {
        super("error.msg.price.identifier.not.found", "price with identifier `" + codeId + "` does not exist", codeId);
    }
    
    public PriceNotFoundException(final Long orderId,final Long clientId) {
    	super("error.msg.price.identifier.not.found", "price does not exist with order id `" + orderId + "` and client id `"+clientId+"`",
    			orderId,clientId);
    }
}