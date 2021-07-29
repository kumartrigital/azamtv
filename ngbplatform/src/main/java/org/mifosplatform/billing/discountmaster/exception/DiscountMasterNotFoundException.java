
package org.mifosplatform.billing.discountmaster.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class DiscountMasterNotFoundException extends AbstractPlatformResourceNotFoundException {

    /**
     * @param discountId
     */
    public DiscountMasterNotFoundException(final Long discountId) {
        super("error.msg.discount.not.found", "Discount with this id"+discountId+"not exist",discountId);
        
    }
    
    public DiscountMasterNotFoundException() {
        super("error.msg.discount.not.found", "No discount found");
    }
   
}