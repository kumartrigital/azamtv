package org.mifosplatform.billing.taxmapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Service;
@Service
public interface TaxMapWritePlatformService {

	 CommandProcessingResult createTaxMap(JsonCommand command);
	 CommandProcessingResult updateTaxMap(JsonCommand command,  Long taxMapId);
	 CommandProcessingResult DeleteTaxMap(Long taxMapId);
}

