package org.mifosplatform.billing.promotioncodes.serialization;

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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class PromotionCodeCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("promotionCode", "promotionDescription","durationType", 
			                            "duration", "discountType", "discountRate","startDate", "locale", "dateFormat"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public PromotionCodeCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	/**
	 * @param json
	 * check validation for create promotion codes
	 */
	public void validateForCreate(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("promotioncode");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String promotioncode = fromApiJsonHelper.extractStringNamed("promotionCode", element);
		baseDataValidator.reset().parameter("promotionCode").value(promotioncode).notBlank().notExceedingLengthOf(10);

		final String promotionDescription = fromApiJsonHelper.extractStringNamed("promotionDescription", element);
		baseDataValidator.reset().parameter("promotionDescription").value(promotionDescription).notBlank();

		final String durationType = fromApiJsonHelper.extractStringNamed("durationType", element);
		baseDataValidator.reset().parameter("durationType").value(durationType).notBlank();
		
		final String discountType = fromApiJsonHelper.extractStringNamed("discountType", element);
		baseDataValidator.reset().parameter("discountType").value(discountType).notBlank();
		
		final BigDecimal discountRate = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("discountRate", element);
		baseDataValidator.reset().parameter("discountRate").value(discountRate).notBlank();

		final Long duration = fromApiJsonHelper.extractLongNamed("duration",element);
		baseDataValidator.reset().parameter("duration").value(duration).notBlank();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}