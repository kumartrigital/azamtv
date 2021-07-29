package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ClientBillInfoNotFoundException extends AbstractPlatformResourceNotFoundException{
	
private static final long serialVersionUID = 1L;
	
public ClientBillInfoNotFoundException(Long id) {  
		
		super("error.msg.clientbillinfo.id.not.found","clientbillinfo is Not Found",id);
	
	}
	
}
