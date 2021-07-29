package org.mifosplatform.crm.ticketmaster.ticketteam.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TicketTeamNotFoundException extends AbstractPlatformResourceNotFoundException {

	
private static final long serialVersionUID = 1L;
	
	public TicketTeamNotFoundException(Long id) {  
		
		super("error.msg.ticketteam.id.not.found","ticketteam is Not Found",id);
	
	}
	
	
	
}
