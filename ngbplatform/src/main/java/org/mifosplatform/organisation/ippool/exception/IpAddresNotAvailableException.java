package org.mifosplatform.organisation.ippool.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * 
 * @author ashokreddy
 *
 */
@SuppressWarnings("serial")
public class IpAddresNotAvailableException extends AbstractPlatformDomainRuleException {

  
    public IpAddresNotAvailableException(final String msg) {
        super("error.msg.ipaddress.are.not.available.please.select.another.iprange", "Ipaddresses are not available please select another iprange", msg);
    }
    
    public IpAddresNotAvailableException() {
        super("error.msg.ipaddress.static.ip.not.available.to.remove", "Static Ipaddresses are not available to this client", "No assigned Staic ips to this client");
    }
    
    public IpAddresNotAvailableException(Long clientId) {
        super("error.msg.ipaddress.static.ip.already.available.to.client", "Static Ipaddresses are available to this client", "Already available assigned Staic ips to this client");
    }
	
}
