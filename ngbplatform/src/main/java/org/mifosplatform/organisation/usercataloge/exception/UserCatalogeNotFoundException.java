package org.mifosplatform.organisation.usercataloge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class UserCatalogeNotFoundException extends AbstractPlatformResourceNotFoundException{

private static final long serialVersionUID = 1L;
	
	public UserCatalogeNotFoundException(Long userId) {
	
		super("error.msg.usercataloge.userId.not.found","usercataloge is Not Found",userId);
	
	
     }
	
}
