package org.mifosplatform.collectionbatch.timemodel.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TimeModelNotFoundException extends AbstractPlatformResourceNotFoundException {
	private static final long serialVersionUID = 1L;

	public TimeModelNotFoundException(Long id) {

		super("error.msg.timemodel.id.not.found", "timemodel is Not Found", id);

	}
}
