package org.mifosplatform.finance.secondarysubscriberdues.service;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface SecondarySubscriberDuesWritePlatformService {
	
	public CommandProcessingResult secondarySubscriberDues(Long clientId,Long officeId, BigDecimal price);


}
