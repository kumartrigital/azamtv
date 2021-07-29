package org.mifosplatform.organisation.salescatalogemapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SalesCatalogeMappingNotFoundException extends AbstractPlatformResourceNotFoundException {

private static final long serialVersionUID = 1L;
	
	public SalesCatalogeMappingNotFoundException(Long id) {
	
		super("error.msg.salescatalogemapping.id.not.found","salescatalogemapping is Not Found",id);
	}
	
	
}
