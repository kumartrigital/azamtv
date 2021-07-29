package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class NoVoucherFoundUnderThisOfficeException extends AbstractPlatformResourceNotFoundException {
	 

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoVoucherFoundUnderThisOfficeException(Long office) {
	        super("error.msg.voucher.not.found", "No Voucher found to under this officeId", office);
	    }

}
