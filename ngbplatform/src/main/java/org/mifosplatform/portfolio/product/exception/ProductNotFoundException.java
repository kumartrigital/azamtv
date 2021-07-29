package org.mifosplatform.portfolio.product.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ProductNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;

	
	public ProductNotFoundException(Long id) {
		
		super("error.msg.product.id.not.found","product is Not Found",id);
		
	}

}
