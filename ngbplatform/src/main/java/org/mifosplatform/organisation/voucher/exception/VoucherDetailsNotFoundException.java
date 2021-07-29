package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * @author rakesh
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class VoucherDetailsNotFoundException extends AbstractPlatformResourceNotFoundException {

    /**
     * @param voucherDetailsId
     */
    public VoucherDetailsNotFoundException(final Long voucherDetailsId) {
        super("error.msg.voucher.not.found", "VoucherDetails with this id"+voucherDetailsId+"not exist",voucherDetailsId);
        
    }
    
    public VoucherDetailsNotFoundException() {
        super("error.msg.voucher.not.found", "No Voucher found or already in use");
    }
   
}
