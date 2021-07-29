package org.mifosplatform.crm.ticketmaster.subcategory.serialization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

import com.google.common.reflect.TypeToken;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */

@Component
public class SubcategoryCommandFromApiJsonDeserializer {

	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("subcategory", "maincategory","timetaken","locale","problemCode"));

	@Autowired
	public SubcategoryCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	public void validateForCreate(final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("dataValidationErrors");

		final JsonElement element = this.fromJsonHelper.parse(json);

		final Integer maincategory = fromJsonHelper.extractIntegerNamed("maincategory", element, supportedParameters);
		baseDataValidator.reset().parameter("maincategory").value(maincategory).notNull();

		final String subcategory = fromJsonHelper.extractStringNamed("subcategory", element);
		baseDataValidator.reset().parameter("subcategory").value(subcategory).notNull();
		
		final Integer timetaken = fromJsonHelper.extractIntegerNamed("timetaken", element, supportedParameters);
		baseDataValidator.reset().parameter("timetaken").value(timetaken).notNull();

	}
	private void throwExceptionIfValidationWarningsExist(List<ApiParameterError> dataValidationErrors) {

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}
