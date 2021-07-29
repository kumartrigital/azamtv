package org.mifosplatform.collectionbatch.usageratebalance.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;

@Component
public class UsageBalanceCommandFromApiJsonDeserializer {
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(
			Arrays.asList("ratePlanId", "tierId", "rum","timeperiodId","glId","uom","unit","rate","currencyId","ratebalance","usageRateplanId","locale"));

	@Autowired
	public UsageBalanceCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}
	
	
public void validateForCreate(String json) {
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
		
		final JsonElement element = this.fromJsonHelper.parse(json);
		
		/*final Long uomId = this.fromJsonHelper.extractLongNamed("uomId", element);
		baseValidatorBuilder.reset().parameter("uomId").value(uomId).notNull().notExceedingLengthOf(100);*/
		
		/*final Long ratePlanId = this.fromJsonHelper.extractLongNamed("ratePlanId", element);
		baseValidatorBuilder.reset().parameter("ratePlanId").value(ratePlanId).notBlank();*/
		
		/*final Long tierId = this.fromJsonHelper.extractLongNamed("tierId", element);
		baseValidatorBuilder.reset().parameter("tierId").value(tierId).notBlank();
		
		final Long rum = this.fromJsonHelper.extractLongNamed("rum", element);
		baseValidatorBuilder.reset().parameter("rum").value(rum).notBlank();
		
		final Long timeperiodId = this.fromJsonHelper.extractLongNamed("timeperiodId", element);
		baseValidatorBuilder.reset().parameter("timeperiodId").value(timeperiodId).notBlank();*/

		/*final Long glId = this.fromJsonHelper.extractLongNamed("glId", element);
		baseValidatorBuilder.reset().parameter("glId").value(glId).notBlank();*/
		
		/*final Long uom = this.fromJsonHelper.extractLongNamed("uom", element);
		baseValidatorBuilder.reset().parameter("uom").value(uom).notBlank();*/
		
		/*final Long unit = this.fromJsonHelper.extractLongNamed("unit", element);
		baseValidatorBuilder.reset().parameter("unit").value(unit).notBlank();
		
		final BigDecimal rate =  this.fromJsonHelper.extractBigDecimalWithLocaleNamed("rate", element);
		baseValidatorBuilder.reset().parameter("rate").value(rate).notNull();*/
		
		final Long currencyId = this.fromJsonHelper.extractLongNamed("currencyId", element);
		baseValidatorBuilder.reset().parameter("currencyId").value(currencyId);
        
		
		this.throwExceptionIfValidationWarningsExist(dataValidationErrors);	
		
		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
            "Validation errors exist.", dataValidationErrors); }
	}
}
