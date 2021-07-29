package org.mifosplatform.portfolio.property.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class PropertyMasterNotFoundException extends AbstractPlatformDomainRuleException {

    /**
     * @param propertyId
     */
    public PropertyMasterNotFoundException(final Long propertyId) {
        super("error.msg.invalid.property.code", "Property Code with this id"+propertyId+"not exist",propertyId);
        
    }
    
    public PropertyMasterNotFoundException(final String propertyCode) {
        super("error.msg.please.free.this.property.from assigned client", "please free this " +propertyCode+ "property from assigned client",propertyCode);
    }
    
    public PropertyMasterNotFoundException(final Long clientId,final String propertyCode) {
    	 super("error.msg.client.address.details.not found.with given property "+propertyCode, "Client address details  not found", clientId);
        
    }
    
   
}