package org.mifosplatform.organisation.hardwareplanmapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class HardwarePlanmappingNotFoundException  extends AbstractPlatformResourceNotFoundException {

	private static final long serialVersionUID = 1L;

	public HardwarePlanmappingNotFoundException() {
		super("error.msg.hardwareplanmapping.id.invalid","Charge Code already exists with same plan");
	}

	public HardwarePlanmappingNotFoundException(Long id) {
		super("error.msg.hardware.mapping.with.id.not.exists","Hardware Plan Mapping was alreay deleted");
	}
}
