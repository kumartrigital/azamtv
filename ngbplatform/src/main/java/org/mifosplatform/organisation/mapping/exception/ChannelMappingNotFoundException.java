package org.mifosplatform.organisation.mapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ChannelMappingNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public ChannelMappingNotFoundException(Long channelmappingId) {
		
		super("error.msg.channel.id.not.found","channel is Not Found",channelmappingId);
	}

}
