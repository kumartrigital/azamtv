package org.mifosplatform.organisation.broadcaster.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class BroadcatserNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public BroadcatserNotFoundException(Long id) {  
		
		super("error.msg.broadcaster.id.not.found","broadcaster is Not Found",id);
	
	}
	
	
	

}
