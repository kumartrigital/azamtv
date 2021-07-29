package org.mifosplatform.collectionbatch.usagerateplan.serialization;

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
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;

@Component
public class RatePlanCommandFromApiJsonDeserializer {
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("planPriceId","timeModelId","rumId","ratingType","locale"));
	
	
	
	@Autowired
	public RatePlanCommandFromApiJsonDeserializer(
			FromJsonHelper fromJsonHelper) {
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
		
		final Long planPriceId = this.fromJsonHelper.extractLongNamed("planPriceId", element);
		baseValidatorBuilder.reset().parameter("planPriceId").value(planPriceId).notNull();
		
		final Long timeModelId = this.fromJsonHelper.extractLongNamed("timeModelId", element);
		baseValidatorBuilder.reset().parameter("timeModelId").value(timeModelId).notNull();
		
		final Long rumId = this.fromJsonHelper.extractLongNamed("rumId", element);
		baseValidatorBuilder.reset().parameter("rumId").value(rumId).notNull();
		
		final String ratingType = this.fromJsonHelper.extractStringNamed("ratingType", element);
		baseValidatorBuilder.reset().parameter("ratingType").value(ratingType).notNull().notExceedingLengthOf(10);
		
		
}
}
