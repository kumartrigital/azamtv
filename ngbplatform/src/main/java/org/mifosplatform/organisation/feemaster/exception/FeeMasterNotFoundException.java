package org.mifosplatform.organisation.feemaster.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class FeeMasterNotFoundException extends AbstractPlatformResourceNotFoundException {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FeeMasterNotFoundException(String id) {
	
				super("error.msg.fee.master.id.not.found", "Fee Master Id "+id+" not found. ",id);
				}
	
	}