package org.mifosplatform.finance.clientbalance.service;

import java.math.BigDecimal;

import org.mifosplatform.finance.clientbalance.data.ConverationDetails;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ClientBalanceWritePlatformService {

	CommandProcessingResult addClientBalance(JsonCommand command);

	BigDecimal conversion(Long baseCurrency, Long conversionCurrency, BigDecimal price);

	ConverationDetails conversionDetails(Long baseCurrency, Long conversionCurrency, BigDecimal price);

}
