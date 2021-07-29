package org.mifosplatform.collectionbatch.timeperiod.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TimePeriodNotFoundException extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;

	public TimePeriodNotFoundException(Long id) {

		super("error.msg.timeperiod.id.not.found", "timeperiod is Not Found", id);

	}
}
