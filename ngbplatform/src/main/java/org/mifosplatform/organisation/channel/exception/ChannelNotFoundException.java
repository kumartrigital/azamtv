package org.mifosplatform.organisation.channel.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ChannelNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public ChannelNotFoundException(Long id) {
	
		super("error.msg.channel.id.not.found","channel is Not Found",id);
	}
	
	

}
