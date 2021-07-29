package org.mifosplatform.collectionbatch.ratableusagemetric.serialization;

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
public class RatableUsageMetricCommandFromApiJsonDeserializer {
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("id","chargeCodeId","templateId","rumName","rumExpression","locale"));
	
	
	
	@Autowired
	public RatableUsageMetricCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
		
		
		final Long chargeCodeId = this.fromJsonHelper.extractLongNamed("chargeCodeId", element);
		baseValidatorBuilder.reset().parameter("chargeCodeId").value(chargeCodeId).notNull().notExceedingLengthOf(10);
		
		final Long templateId = this.fromJsonHelper.extractLongNamed("templateId", element);
		baseValidatorBuilder.reset().parameter("templateId").value(templateId).notNull().notExceedingLengthOf(100);
		
		final String rumName = fromJsonHelper.extractStringNamed("rumName", element);
		baseValidatorBuilder.reset().parameter("rumName").value(rumName).notNull().notExceedingLengthOf(100);
		
		
		final String rumExpression = fromJsonHelper.extractStringNamed("rumExpression", element);
		baseValidatorBuilder.reset().parameter("rumExpression").value(rumExpression).notNull().notExceedingLengthOf(100);
		
		this.throwExceptionIfValidationWarningsExist(dataValidationErrors);	
		
		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
            "Validation errors exist.", dataValidationErrors); }
	}
	

}
