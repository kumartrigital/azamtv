package org.mifosplatform.organisation.partner.service;

import java.io.InputStream;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;

public interface PartnersWritePlatformService {

	CommandProcessingResult createNewPartner(JsonCommand command);
	
	CommandProcessingResult updatePartner(JsonCommand command,Long partnerId);

	CommandProcessingResult saveOrUpdatePartnerImage(Long partnerId,
			String fileName, InputStream inputStream);

	CommandProcessingResult saveOrUpdatePartnerImage(Long partnerId,
			Base64EncodedImage base64EncodedImage);
	
}
