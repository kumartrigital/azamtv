package org.mifosplatform.portfolio.property.service;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class SerianumberMappingNotFoundException extends AbstractPlatformDomainRuleException  {
	
	private static final long serialVersionUID = 1L;
	
	public SerianumberMappingNotFoundException (final String serialNumber,final String oldPropertyCode)
	{
		super("error.msg.serianumber.isnot.mapped", serialNumber + "Please map the device with proprtycode: "+ oldPropertyCode);
	}

}
