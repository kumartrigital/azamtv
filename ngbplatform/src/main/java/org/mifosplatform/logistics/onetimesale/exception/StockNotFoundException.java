/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.logistics.onetimesale.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a device one time sale is not found.
 */
public class StockNotFoundException extends AbstractPlatformResourceNotFoundException {

    /* * */
	private static final long serialVersionUID = 1L;

	public StockNotFoundException() {
        super("error.msg.device.one time stock.not.found", "stock not found in central stock");
        
    }

}