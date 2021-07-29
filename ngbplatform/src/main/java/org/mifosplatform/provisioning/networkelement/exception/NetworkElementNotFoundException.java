package org.mifosplatform.provisioning.networkelement.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;




public class NetworkElementNotFoundException extends AbstractPlatformResourceNotFoundException{
	
private static final long serialVersionUID = 1L;
	
	public NetworkElementNotFoundException(Long id) {
	
		super("error.msg.networkelement.id.not.found","networkelement is Not Found",id);
	}

}
