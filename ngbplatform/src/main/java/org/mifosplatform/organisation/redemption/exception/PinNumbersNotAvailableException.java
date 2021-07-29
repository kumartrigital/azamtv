package org.mifosplatform.organisation.redemption.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class PinNumbersNotAvailableException extends AbstractPlatformResourceNotFoundException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PinNumbersNotAvailableException(final String message) {
		super("error.msg.voucher.notfound" + "product stock Not Available with online dealer" , "product stock Not Available with online dealer");
    }
	


	public PinNumbersNotAvailableException() {
		super("error.msg.voucher.notfound" + "Unable To Process the Request" , "Unable To Process the Request");
    }
	
}
