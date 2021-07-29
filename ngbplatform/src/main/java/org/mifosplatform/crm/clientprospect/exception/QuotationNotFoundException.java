package org.mifosplatform.crm.clientprospect.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class QuotationNotFoundException extends AbstractPlatformResourceNotFoundException  {

	private static final long serialVersionUID = 1L;
	
public QuotationNotFoundException(Long id) {
		
		super("error.msg.Quotation.id.not.found","quotation is Not Found",id);
	}
}
