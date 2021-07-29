package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ExportingRequestFailed extends AbstractPlatformResourceNotFoundException {

	public ExportingRequestFailed() {
		super("error.msg.export.not.found", "export failed");
	}

	public ExportingRequestFailed(String message) {
		super("error.msg.quatity.not.found", message);
	}
}
