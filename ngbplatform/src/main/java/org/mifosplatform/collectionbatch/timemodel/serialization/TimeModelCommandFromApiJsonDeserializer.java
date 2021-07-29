package org.mifosplatform.collectionbatch.timemodel.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;

@Component
public class TimeModelCommandFromApiJsonDeserializer {
	private FromJsonHelper fromJsonHelper;

	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("timeModelName","times","description","locale","startYear","timemodelName"));

	
	@Autowired
	public TimeModelCommandFromApiJsonDeserializer(
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
		

		final String timeModelName = this.fromJsonHelper.extractStringNamed("timeModelName", element);
		baseValidatorBuilder.reset().parameter("timeModelName").value(timeModelName).notNull().notExceedingLengthOf(10);
		
		final String description = this.fromJsonHelper.extractStringNamed("description", element);
		baseValidatorBuilder.reset().parameter("description").value(description).notNull().notExceedingLengthOf(100);
		
		/*final Long startYear = this.fromJsonHelper.extractLongNamed("startYear", element);*/
		/*LocalDate localDate = new LocalDate(new Date());
		long year =localDate.getYear();
		if(startYear<year) {
			dataValidationErrors.add(ApiParameterError.parameterError("staryear should not be lessthan present year","staryear should not be lessthan present year", "staryear should not be lessthan present year",""));
		}*/
		
		
}
}
