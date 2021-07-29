package org.mifosplatform.collectionbatch.usageratequantitytier.serialization;

import java.lang.reflect.Type;
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
public class UsageRateQuantityTierCommandFromApiJsonDeserializer {
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("usageRateplanId","startRange","endRange","tierName","locale","range"));
	
	
	@Autowired
	public UsageRateQuantityTierCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}
	
	
	

	public void validateForCreate(String json) {if(StringUtils.isBlank(json)){
		throw new InvalidJsonException();
	}
	final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
	this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
	
	final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
	
	final JsonElement element = this.fromJsonHelper.parse(json);
	
	final Long usageRateplanId = this.fromJsonHelper.extractLongNamed("usageRateplanId", element);
	baseValidatorBuilder.reset().parameter("usageRateplanId").value(usageRateplanId).notNull().notExceedingLengthOf(100);
	
	/*final Long startRange = this.fromJsonHelper.extractLongNamed("startRange", element);
	baseValidatorBuilder.reset().parameter("startRange").value(startRange).notNull().notExceedingLengthOf(100);
	
	
	final Long endRange = this.fromJsonHelper.extractLongNamed("endRange", element);
	baseValidatorBuilder.reset().parameter("endRange").value(endRange).notNull().notExceedingLengthOf(100);*/
	
	/*final String tierName = this.fromJsonHelper.extractStringNamed("tierName", element);
	baseValidatorBuilder.reset().parameter("tierName").value(tierName).notNull().notExceedingLengthOf(100);*/
	
	this.throwExceptionIfValidationWarningsExist(dataValidationErrors);	
	
	
	}

private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
        "Validation errors exist.", dataValidationErrors); }
}

}
