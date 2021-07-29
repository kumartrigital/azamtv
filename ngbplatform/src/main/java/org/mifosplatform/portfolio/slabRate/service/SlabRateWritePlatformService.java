package org.mifosplatform.portfolio.slabRate.service;

import java.math.BigDecimal;


import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Service;


public interface SlabRateWritePlatformService {

	public CommandProcessingResult Calculation(String jsonRequestBody);
	public CommandProcessingResult prepaidService(Long  clientId ,BigDecimal price );

}
