package org.mifosplatform.organisation.salescataloge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SalesCatalogeNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public SalesCatalogeNotFoundException(Long id) {
		
		super("error.msg.salescataloge.id.not.found","salescataloge is Not Found",id);
	}

}
