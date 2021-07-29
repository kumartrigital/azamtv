package org.mifosplatform.collectionbatch.template.serialization;

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
public class TemplatesCommandFromApiJsonDeserializer {
	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("templateName", "delimited",
			"delimiter", "numberOfFields", "isHeader", "headerRecordType", "eventRecordType","columns","fieldName","fieldType","length","identifierType","locale"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public TemplatesCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	/**
	 * @param json
	 *            check validation for create template
	 */
	public void validateForCreate(final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("template");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String name = fromApiJsonHelper.extractStringNamed("templateName", element);
		baseDataValidator.reset().parameter("templateName").value(name).notBlank().notExceedingLengthOf(100);

		/*final Long delimited = fromApiJsonHelper.extractLongNamed("delimited", element);
		baseDataValidator.reset().parameter("delimited").value(delimited).notNull();*/

		final String delimiter = fromApiJsonHelper.extractStringNamed("delimiter", element);
		baseDataValidator.reset().parameter("delimiter").value(delimiter).notBlank().notExceedingLengthOf(100);

		final Long numberOfFields = fromApiJsonHelper.extractLongNamed("numberOfFields", element);
		baseDataValidator.reset().parameter("numberOfFields").value(numberOfFields).notNull();
		final String headerRecordType = fromApiJsonHelper.extractStringNamed("headerRecordType", element);
		baseDataValidator.reset().parameter("headerRecordType").value(headerRecordType).notBlank()
				.notExceedingLengthOf(100);

		final String eventRecordType = fromApiJsonHelper.extractStringNamed("eventRecordType", element);
		baseDataValidator.reset().parameter("eventRecordType").value(eventRecordType).notBlank()
				.notExceedingLengthOf(100);

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}
