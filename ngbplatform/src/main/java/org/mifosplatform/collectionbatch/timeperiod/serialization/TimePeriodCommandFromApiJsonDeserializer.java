package org.mifosplatform.collectionbatch.timeperiod.serialization;

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
public class TimePeriodCommandFromApiJsonDeserializer {
	private FromJsonHelper fromJsonHelper;

	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("timeperiodName", "timemodelId",
			"startYear", "endYear", "startMonth", "endMonth", "startDay", "endDay", "startTime", "endTime"));

	@Autowired
	public TimePeriodCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	public void validateForCreate(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);

		final JsonElement element = this.fromJsonHelper.parse(json);

		final String timeperiodName = this.fromJsonHelper.extractStringNamed("timeperiodName", element);
		baseValidatorBuilder.reset().parameter("timeModelName").value(timeperiodName).notNull()
				.notExceedingLengthOf(10);

		final Long timemodelId = this.fromJsonHelper.extractLongNamed("timemodelId", element);
		baseValidatorBuilder.reset().parameter("timemodelId").value(timemodelId).notNull().notExceedingLengthOf(100);

		final Long startYear = this.fromJsonHelper.extractLongNamed("startYear", element);
		baseValidatorBuilder.reset().parameter("startYear").value(startYear).notNull().notExceedingLengthOf(100);

		final Long endYear = this.fromJsonHelper.extractLongNamed("endYear", element);
		baseValidatorBuilder.reset().parameter("endYear").value(endYear).notNull().notExceedingLengthOf(100);

		final String startMonth = this.fromJsonHelper.extractStringNamed("startMonth", element);
		baseValidatorBuilder.reset().parameter("startMonth").value(startMonth).notNull().notExceedingLengthOf(100);

		final String endMonth = this.fromJsonHelper.extractStringNamed("endMonth", element);
		baseValidatorBuilder.reset().parameter("endMonth").value(endMonth).notNull().notExceedingLengthOf(100);

		/*
		 * final Long startYear = this.fromJsonHelper.extractLongNamed("startYear",
		 * element);
		 */
		/*
		 * LocalDate localDate = new LocalDate(new Date()); long year
		 * =localDate.getYear(); if(startYear<year) {
		 * dataValidationErrors.add(ApiParameterError.
		 * parameterError("staryear should not be lessthan present year"
		 * ,"staryear should not be lessthan present year",
		 * "staryear should not be lessthan present year","")); }
		 */

	}
}
