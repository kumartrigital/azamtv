package org.mifosplatform.collectionbatch.unitofmeasurement.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class UnitOfmeasurementNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public UnitOfmeasurementNotFoundException(Long id) {  
		
		super("error.msg.UnitOfmeasurement.id.not.found","UnitOfmeasurement is Not Found",id);
	
	}

}
