package org.mifosplatform.organisation.mcodevalues.service;

import java.util.Collection;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public interface MCodeReadPlatformService {

	public Collection<MCodeData> getCodeValue(final String codeName);
	
	public Collection<MCodeData> getCodeValue(final String codeName,final String orderPosition);
	
	public Collection<MCodeData> retriveMcode(String query);

}
