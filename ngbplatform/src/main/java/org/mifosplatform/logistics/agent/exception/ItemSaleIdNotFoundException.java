package org.mifosplatform.logistics.agent.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ItemSaleIdNotFoundException extends AbstractPlatformResourceNotFoundException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemSaleIdNotFoundException(final Long id) {
        super("error.msg.itemsale.id.invalid", "itemsale with identifier " + id + " does not exist", id);
    }
}
