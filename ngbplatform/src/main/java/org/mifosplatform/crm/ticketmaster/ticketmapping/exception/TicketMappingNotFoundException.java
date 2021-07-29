package org.mifosplatform.crm.ticketmaster.ticketmapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TicketMappingNotFoundException  extends AbstractPlatformResourceNotFoundException{

	
private static final long serialVersionUID = 1L;
	
	public TicketMappingNotFoundException(Long id) {  
		
		super("error.msg.ticketmapping.id.not.found","ticketmapping is Not Found",id);
	
	}
	
}
