package org.mifosplatform.billing.chargecode.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * @author hugo
 * 
 *         this class {@link RuntimeException} thrown when a code is not found.
 */

public class ChargeCodeNotFoundException extends
		AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param chargeCodeId
	 */
	public ChargeCodeNotFoundException(final String chargeCodeId) {
		super("error.msg.chargeCode.not.found", "chargeCode with this id"
				+ chargeCodeId + "not exist", chargeCodeId);

	}

}