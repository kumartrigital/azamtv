package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class NoMoreRecordsFoundToExportException extends AbstractPlatformResourceNotFoundException {

	public NoMoreRecordsFoundToExportException() {
		super("error.msg.voucher.not.found", "No Voucher found to export or already exported");
	}

	public NoMoreRecordsFoundToExportException(String message) {
		super("error.msg.quatity.not.found", message);
	}
}
