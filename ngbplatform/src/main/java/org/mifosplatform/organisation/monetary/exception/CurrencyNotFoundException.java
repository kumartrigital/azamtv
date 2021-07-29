/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when loan resources are not found.
 */
@SuppressWarnings("serial")
public class CurrencyNotFoundException extends
		AbstractPlatformResourceNotFoundException {

	public CurrencyNotFoundException(final String string) {
		super("error.msg.currency.currencyCode.invalid",
				"Currency with identifier " + string + " does not exist",
				string);
	}

	public CurrencyNotFoundException(Long id) {
		
		super("error.msg.currency.id.not.found","currency is Not Found",id);
		
		
	}

	
}