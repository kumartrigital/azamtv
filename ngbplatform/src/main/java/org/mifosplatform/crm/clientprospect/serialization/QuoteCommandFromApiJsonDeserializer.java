package org.mifosplatform.crm.clientprospect.serialization;

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
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Component
public final class QuoteCommandFromApiJsonDeserializer {


	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("leadId","dateFormat","quoteDate","locale","quoteStatus","totalCharge","Notes","servicePlanDetails","planRecurirngCharge","planonetimeCharge","quoteNo","chargeCode"));
	
	@Autowired
	public QuoteCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}
	
	
	
public void validateForCreate(String json) {
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
		
		final JsonElement element = this.fromJsonHelper.parse(json);
		
		final Long leadId = fromJsonHelper.extractLongNamed("leadId", element);
		baseValidatorBuilder.reset().parameter("leadId").value(leadId);
		
		final LocalDate quoteDate = fromJsonHelper.extractLocalDateNamed("quoteDate", element);
		baseValidatorBuilder.reset().parameter("quoteDate").value(quoteDate);
				
		final String quoteStatus = fromJsonHelper.extractStringNamed("quoteStatus", element);
		baseValidatorBuilder.reset().parameter("quoteStatus").value(quoteStatus);
		
		final BigDecimal totalCharge= fromJsonHelper.extractBigDecimalWithLocaleNamed("totalCharge", element);
		baseValidatorBuilder.reset().parameter("totalCharge").value(totalCharge);
		
		final String Notes = fromJsonHelper.extractStringNamed("Notes", element);
		baseValidatorBuilder.reset().parameter("Notes").value(Notes);
		
		final String quoteNo = fromJsonHelper.extractStringNamed("quoteNo", element);
		baseValidatorBuilder.reset().parameter("quoteNo").value(quoteNo);
		
		final String chargeCode = fromJsonHelper.extractStringNamed("chargeCode", element);
		baseValidatorBuilder.reset().parameter("chargeCode").value(chargeCode);
		
		final JsonArray servicePlanDetails = fromJsonHelper.extractJsonArrayNamed("servicePlanDetails", element);
		baseValidatorBuilder.reset().parameter("servicePlanDetails").value(servicePlanDetails).jsonArrayNotEmpty();
        
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);		
			
	}

private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
            "Validation errors exist.", dataValidationErrors); }

	
}
}
