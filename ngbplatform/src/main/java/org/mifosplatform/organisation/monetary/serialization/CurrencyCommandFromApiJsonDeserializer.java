/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.serialization;

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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class CurrencyCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final FromJsonHelper fromApiJsonHelper;
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("code","name","decimalPlaces","nameCode"
			   ,"displaySymbol","countryCode","countryName","type","locale"));

	

	@Autowired
	public CurrencyCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public void validateForCreate(final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("currencies");

		final JsonElement element = fromApiJsonHelper.parse(json);
		
		
		final String code = fromApiJsonHelper.extractStringNamed("code", element);
		baseDataValidator.reset().parameter("code").value(code).notNull().notExceedingLengthOf(3);

		final String name = fromApiJsonHelper.extractStringNamed("name", element);
		baseDataValidator.reset().parameter("name").value(name).notNull().notExceedingLengthOf(50);
		
		final Long decimalPlaces = fromApiJsonHelper.extractLongNamed("decimalPlaces", element);
		baseDataValidator.reset().parameter("decimalPlaces").value(decimalPlaces).notNull().notExceedingLengthOf(10);

		final String type = fromApiJsonHelper.extractStringNamed("type", element);
		baseDataValidator.reset().parameter("type").value(type).notNull().notExceedingLengthOf(50);
		
	
		if("Currency".equalsIgnoreCase(type)){
			final String nameCode = fromApiJsonHelper.extractStringNamed("nameCode", element);
			final String displaySymbol = fromApiJsonHelper.extractStringNamed("displaySymbol", element);
			final String countryCode = fromApiJsonHelper.extractStringNamed("countryCode", element);
			final String countryName = fromApiJsonHelper.extractStringNamed("countryName", element);
				
			baseDataValidator.reset().parameter("nameCode").value(nameCode).notNull().notExceedingLengthOf(50);
			baseDataValidator.reset().parameter("displaySymbol").value(displaySymbol).notNull().notExceedingLengthOf(10);
			baseDataValidator.reset().parameter("countryCode").value(countryCode).notNull().notExceedingLengthOf(3);
			baseDataValidator.reset().parameter("countryName").value(countryName).notNull().notExceedingLengthOf(50);
		}
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(
			final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}