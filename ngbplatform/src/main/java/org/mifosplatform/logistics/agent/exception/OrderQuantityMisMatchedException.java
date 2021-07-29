package org.mifosplatform.logistics.agent.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class OrderQuantityMisMatchedException extends AbstractPlatformResourceNotFoundException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public OrderQuantityMisMatchedException(Long itemSaleorderQuantity, Long Movingquantity) {
		// TODO Auto-generated constructor stub
		super("error.msg.itemsale.quantity.mismacth", "itemsale with order quantity " + itemSaleorderQuantity + " and request for moving quantity "+Movingquantity+ "mis macthing", "");
	}
}
