package org.mifosplatform.logistics.itemdetails.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class OrderQuantityExceedsException extends AbstractPlatformDomainRuleException {

    

	public OrderQuantityExceedsException(Long orderId) {
		 super("error.msg.order.quantity..exceeds", "No more order quantity for grn id "+orderId,orderId);
		 
	}
	
	public OrderQuantityExceedsException() {
		 super("error.msg.edit.items.assign.to.customer", "Items assigned to customer, unable to edit it");
		 
	}
	
	public OrderQuantityExceedsException(String msg) {
		 super("error.msg.delete.items.assign.to.customer", "Items assigned to customer, unable to delete it"+msg);
		 
	}
}
