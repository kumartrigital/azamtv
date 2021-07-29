package org.mifosplatform.portfolio.contract.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ContractPeriodNotFoundException extends AbstractPlatformDomainRuleException {

	public ContractPeriodNotFoundException(final String duration,final Long clientId) {
        super("error.msg.duration.not.found", "Duration not found for this " + duration + " and clientId `" + clientId+"`", duration,clientId);
    }
    
    public ContractPeriodNotFoundException(final String duration,final Long orderId,final Long clientId) {
    	super("error.msg.given.duration.not.equal", "Given duration `"+duration+"` is not equal with the orderId `" + orderId + "` and clientId `" + clientId+"`", orderId,clientId);
    }

}
