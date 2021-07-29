package org.mifosplatform.organisation.partneragreement.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PartnersAgreementWritePlatformService {

	CommandProcessingResult createNewPartnerAgreement(JsonCommand command);

	CommandProcessingResult UpdatePartnerAgreement(JsonCommand command);

	//CommandProcessingResult deletePartnerAgreement(Long entityId);
	
	CommandProcessingResult deletePartnerAgreement(JsonCommand command);

	
}
