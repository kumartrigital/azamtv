package org.mifosplatform.billing.chargecode.serialization;

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

/**
 * @author hugo Deserializer for code JSON to validate API request.
 */
@Component
public class ChargeCodeCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	final private Set<String> supportedParameters = new HashSet<String>(Arrays.asList("billFrequencyCode", "chargeCode","chargeDescription", "chargeDuration", "chargeType",
					"dateFormat", "durationType", "locale", "taxInclusive","isAggregate"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public ChargeCodeCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	/**
	 * @param json
	 * check validation for create charge codes
	 */

	public void validaForCreate(String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("chargecode");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String chargeCode = fromApiJsonHelper.extractStringNamed("chargeCode", element);
		if(chargeCode!=null && chargeCode!=""){
		if ( !chargeCode.matches("[a-zA-Z]+")){

			dataValidationErrors.add(ApiParameterError.parameterError(" special characters and numbers not allowed in chargeCode"," special characters and numbers not allowed in chargeCode" , "chargeCode" ,chargeCode));

			 }
		 }
		baseDataValidator.reset().parameter("chargeCode").value(chargeCode).notBlank().notExceedingLengthOf(10);

		final String chargeDescription = fromApiJsonHelper.extractStringNamed("chargeDescription", element);
		baseDataValidator.reset().parameter("chargeDescription").value(chargeDescription).notBlank();

		final String chargeType = fromApiJsonHelper.extractStringNamed("chargeType", element);
		baseDataValidator.reset().parameter("chargeType").value(chargeType).notBlank();
		
		final Integer chargeDuration = fromApiJsonHelper.extractIntegerWithLocaleNamed("chargeDuration", element);
		baseDataValidator.reset().parameter("chargeDuration").value(chargeDuration).notBlank().integerGreaterThanZero();
	
		final String durationType = fromApiJsonHelper.extractStringNamed("durationType", element);
		baseDataValidator.reset().parameter("durationType").value(durationType).notBlank();

		final boolean taxInclusive = fromApiJsonHelper.extractBooleanNamed("taxInclusive", element);
		baseDataValidator.reset().parameter("taxInclusive").value(taxInclusive).notBlank();
		
		final String billFrequencyCode = fromApiJsonHelper.extractStringNamed("billFrequencyCode", element);
		baseDataValidator.reset().parameter("billFrequencyCode").value(billFrequencyCode).notBlank();
		
		final boolean isAggregate = fromApiJsonHelper.extractBooleanNamed("isAggregate", element);
		baseDataValidator.reset().parameter("isAggregate").value(isAggregate).notBlank();
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

	private void throwExceptionIfValidationWarningsExist(
			final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}
