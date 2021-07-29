package org.mifosplatform.organisation.address.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class CityNotFoundException extends AbstractPlatformResourceNotFoundException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CityNotFoundException(final String id) {
        super("error.msg.city.not.found", "City with this id " +id+" not exist",id);
        
    }

}
