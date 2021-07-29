package org.mifosplatform.finance.clientbalance.service;

import java.math.BigDecimal;

import org.mifosplatform.billing.currency.domain.CountryCurrency;
import org.mifosplatform.billing.currency.domain.CountryCurrencyRepository;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.clientbalance.serialization.ClientBalanceCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public  class ClientBalanceWritePlatformServiceImpl implements ClientBalanceWritePlatformService{

	private final PlatformSecurityContext context;
	 private final ClientBalanceCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private ClientBalanceRepository clientBalanceRepository;
	private CountryCurrencyRepository countryCurrencyRepository;

	@Autowired
	 public ClientBalanceWritePlatformServiceImpl(final PlatformSecurityContext context,
			 final ClientBalanceRepository clientBalanceRepository,final ClientBalanceCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			 final CountryCurrencyRepository countryCurrencyRepository)
	{
		 this.context=context;
		 this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		 this.clientBalanceRepository=clientBalanceRepository;
		 this.countryCurrencyRepository=countryCurrencyRepository;
	}

	@Override
	public CommandProcessingResult addClientBalance(JsonCommand command) {
		try
		{
		context.authenticatedUser();
		   this.fromApiJsonDeserializer.validateForCreate(command.json());
		final ClientBalance clientBalance = ClientBalance.fromJson(command);
		this.clientBalanceRepository.save(clientBalance);
			return new CommandProcessingResult(clientBalance.getId());

	} catch (DataIntegrityViolationException dve) {
		 handleCodeDataIntegrityIssues(command, dve);
		return  CommandProcessingResult.empty();
	}
}
		private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {

	        Throwable realCause = dve.getMostSpecificCause();
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
		}
	
		
	@Override
	public BigDecimal conversion( Long baseCurrency,Long conversionCurrency,BigDecimal price) {
		try
		{
		BigDecimal conversionPrice = price;
		/*if(conversionCurrency>baseCurrency||conversionCurrency<baseCurrency) {*/
		if(!baseCurrency.equals(conversionCurrency)) {
		CountryCurrency countryCurrency = this.countryCurrencyRepository.findByCurrencyAndBaseCurrency(conversionCurrency,baseCurrency);
		BigDecimal conversionRate = countryCurrency.getConversionRate();
		conversionPrice = conversionPrice.multiply(conversionRate);
		
		}
		
		return conversionPrice;
	}catch(Exception e){
        throw new PlatformDataIntegrityException("please do create currencyexchange", e.getMessage());
    }
		
	
		
}
}	
