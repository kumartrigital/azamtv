package org.mifosplatform.portfolio.activationprocess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PhotoNotFoundException  extends AbstractPlatformDomainRuleException{
	
	
	private static final long serialVersionUID = 1L;

	public PhotoNotFoundException(String image) {
		super("error.msg.photo", "Photo is required",image);
	}

}
