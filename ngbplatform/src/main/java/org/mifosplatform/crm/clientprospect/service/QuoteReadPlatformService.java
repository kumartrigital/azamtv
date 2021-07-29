package org.mifosplatform.crm.clientprospect.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.data.QuoteData;

public interface QuoteReadPlatformService {

	public List<QuoteData> retrievePlans(Long serviceId);
  
	public List<QuoteData> retrievePlansPricing(Long planId,String chargecode);
	
	public List<QuoteData> retrivequotes(Long leadId,Long quoteId);
	
	public List<QuoteData> statusquotes(Long leadId);
}
