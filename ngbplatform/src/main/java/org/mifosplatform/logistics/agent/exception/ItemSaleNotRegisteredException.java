package org.mifosplatform.logistics.agent.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ItemSaleNotRegisteredException extends AbstractPlatformResourceNotFoundException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemSaleNotRegisteredException(final Long id) {
        super("error.msg.itemsale.officeId.invalid", "itemsale for this office " + id + "doesn't exist", id);
    }

}
