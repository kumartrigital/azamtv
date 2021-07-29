package org.mifosplatform.organisation.address.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class DistrictNotFoundException extends AbstractPlatformResourceNotFoundException {

	
	private static final long serialVersionUID = 1L;
	
	public DistrictNotFoundException(final String id)
			 {
		
		  super("error.msg.district.not.found", "district with this id"+id+"not exist",id);
		// TODO Auto-generated constructor stub
	}

	


}
