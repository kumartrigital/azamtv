package org.mifosplatform.organisation.ippool.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * 
 * @author ashokreddy
 *
 */
@SuppressWarnings("serial")
public class IpNotAvailableException extends AbstractPlatformDomainRuleException {

    public IpNotAvailableException(final String ipAddress) {
        super("error.msg.ipaddress.not.available.to.assign", "Ipaddress is not available to assign", ipAddress);
    }

	public IpNotAvailableException(final Long orderId) {
		 super("error.msg.duplicate.ipaddresses.are.not allow.to.assign", "Duplicate ip's are not allow to assign", orderId);
	}
    
}
