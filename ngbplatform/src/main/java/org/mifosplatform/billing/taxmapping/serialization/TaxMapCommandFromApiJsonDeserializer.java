package org.mifosplatform.billing.taxmapping.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.taxmapping.data.TaxMapData;
import org.mifosplatform.billing.taxmapping.service.TaxMapReadPlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;


/**
 * Deserializer for code JSON to validate API request.
 */

@Component
public final class TaxMapCommandFromApiJsonDeserializer {

	  /**
     * The parameters supported for this command.
     */
	
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("chargeCode","taxCode","startDate","taxType","rate","locale","dateFormat","taxRegion"));
	
	private final FromJsonHelper fromJsonHelper;
	private final TaxMapReadPlatformService taxMapReadPlatformService;
	
	@Autowired
	public TaxMapCommandFromApiJsonDeserializer(final FromJsonHelper fromJsonHelper,final TaxMapReadPlatformService taxMapReadPlatformService) {
		this.fromJsonHelper = fromJsonHelper;
		this.taxMapReadPlatformService = taxMapReadPlatformService;
	}
	
	public void validateForCreate(final JsonCommand command){
		
		if(StringUtils.isBlank(command.toString())){
			throw new InvalidJsonException();
		}
		
		final Type typeOfMap = new TypeToken<Map<String,Object>>(){}.getType();
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, command.json(), supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("taxmap");

		List<TaxMapData>  taxCodesList=this.taxMapReadPlatformService.retriveTaxMapingData();
		
		 List<String> taxCodes = new ArrayList<>();
		 
		 for(TaxMapData TaxMapData : taxCodesList){
			 taxCodes.add(TaxMapData.getTaxCode());
	        	
	        }
		
		final String taxCode = command.stringValueOfParameterNamed("taxCode");
		baseDataValidator.reset().parameter("taxCode").value(taxCode).notBlank().notExceedingLengthOf(10);
		
		 boolean contains = taxCodes.contains(taxCode);
         if(contains)
         	dataValidationErrors.add(ApiParameterError.parameterError("Duplicate taxCode","Duplicate taxCode", "taxCode",taxCode));
         
		final Long taxRegion=command.longValueOfParameterNamed("taxRegion");
		baseDataValidator.reset().parameter("taxRegion").value(taxRegion).notBlank();
		
		final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
		baseDataValidator.reset().parameter("startDate").value(startDate).notBlank();
		
		final String taxType = command.stringValueOfParameterNamed("taxType");
		if(taxType.contains("-1"))
			baseDataValidator.reset().parameter("taxType").value(taxType).notBlank().notExceedingLengthOf(15).zeroOrPositiveAmount();
		else
			baseDataValidator.reset().parameter("taxType").value(taxType).notBlank().notExceedingLengthOf(15);
		
		final BigDecimal rate = command.bigDecimalValueOfParameterNamed("rate"); 
		baseDataValidator.reset().parameter("rate").value(rate).notBlank();
	
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}
