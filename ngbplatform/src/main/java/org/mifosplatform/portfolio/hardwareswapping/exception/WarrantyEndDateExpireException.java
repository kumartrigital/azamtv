package org.mifosplatform.portfolio.hardwareswapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class WarrantyEndDateExpireException  extends AbstractPlatformDomainRuleException {

	public WarrantyEndDateExpireException(String serialNo) {
		
		super("error.msg.hardware.swapping.warranty.expiry", "Couldn't swap Hardware because hardware warranty date expired", serialNo);
	}

}
