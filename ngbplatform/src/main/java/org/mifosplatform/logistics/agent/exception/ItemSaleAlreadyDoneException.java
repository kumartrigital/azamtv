package org.mifosplatform.logistics.agent.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ItemSaleAlreadyDoneException extends AbstractPlatformResourceNotFoundException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemSaleAlreadyDoneException(final Long id) {
        super("error.msg.itemsale.id.invalid", "itemsale with identifier " + id + " is completed already", id);
    }

}
