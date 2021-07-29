package org.mifosplatform.crm.clientprospect.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface QuoteWritePlatformService {

	public CommandProcessingResult createQuote(JsonCommand command);
    
	public CommandProcessingResult deleteQuotation(JsonCommand command, Long entityId);

	public CommandProcessingResult updateQuotation(JsonCommand command, Long quoteId);
	
	public CommandProcessingResult updateQuotationStatus(JsonCommand command, Long entityId);
	
	String generateQuoteStatementPdf(Long leadId);

}
