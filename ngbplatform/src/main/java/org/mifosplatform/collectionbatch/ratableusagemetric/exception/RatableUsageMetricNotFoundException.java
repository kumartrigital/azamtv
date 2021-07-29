package org.mifosplatform.collectionbatch.ratableusagemetric.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RatableUsageMetricNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public RatableUsageMetricNotFoundException(Long id) {  
		
		super("error.msg.ratableusagemetric.id.not.found","ratableusagemetric is Not Found",id);
	
	}

}
