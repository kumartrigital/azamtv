package org.mifosplatform.organisation.partneragreement.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class PartnersAgreementCommandFromApiJsonDeserializer {

	/*
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("agreementStatus", "shareType", "shareAmount","sourceType","startDate",
			                                               "endDate","sourceData","locale","dateFormat","removeSourceData","billAlign","planCode","contractPeriod","paytermCode","saleDate","officeId","planPoId","dealPoId","poId","settlementPoId","isPrepaid","clientId","clientServiceId","plans"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public PartnersAgreementCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	/**
	 * @param json
	 *            check validation for create agreement
	 */
	public void validateForCreate(final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("agreement");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String agreementStatus = fromApiJsonHelper.extractStringNamed("agreementStatus", element);
		baseDataValidator.reset().parameter("agreementStatus").value(agreementStatus).notBlank().notExceedingLengthOf(50);

		final LocalDate startDate = fromApiJsonHelper.extractLocalDateNamed("startDate", element);
		baseDataValidator.reset().parameter("startDate").value(startDate).notBlank();

		final Long planCode = fromApiJsonHelper.extractLongNamed("planCode", element);
		baseDataValidator.reset().parameter("planCode").value(planCode).notBlank();

		final Long contractPeriod = fromApiJsonHelper.extractLongNamed("contractPeriod", element);
		baseDataValidator.reset().parameter("contractPeriod").value(contractPeriod).notBlank();

		final String paytermCode = fromApiJsonHelper.extractStringNamed("paytermCode",element);
		baseDataValidator.reset().parameter("paytermCode").value(paytermCode).notBlank();

		final JsonArray partnerAgreementDataArray = fromApiJsonHelper.extractJsonArrayNamed("sourceData", element);
		int DataSize = partnerAgreementDataArray.size();
		baseDataValidator.reset().parameter("sourceData").value(DataSize).integerGreaterThanZero();

		if (partnerAgreementDataArray != null && partnerAgreementDataArray.size() > 0) {
			String[] paramsDataArrayAttributes = null;
			paramsDataArrayAttributes = new String[partnerAgreementDataArray.size()];

			for (int i = 0; i < partnerAgreementDataArray.size(); i++) {

				paramsDataArrayAttributes[i] = partnerAgreementDataArray.get(i).toString();
			}

			for (JsonElement jsonElement : partnerAgreementDataArray) {


				/*final Long source = fromApiJsonHelper.extractLongNamed("source", jsonElement);
				baseDataValidator.reset().parameter("source").value(source).notBlank();

				final String shareType = fromApiJsonHelper.extractStringNamed("shareType", jsonElement);
				baseDataValidator.reset().parameter("shareType").value(shareType).notBlank().notExceedingLengthOf(20);

				final BigDecimal shareAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("shareAmount",jsonElement);
				baseDataValidator.reset().parameter("shareAmount").value(shareAmount).notBlank();*/

				
			}
		}

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}
	
	public void validateForUpdate(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("agreement");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String agreementStatus = fromApiJsonHelper.extractStringNamed("agreementStatus", element);
		baseDataValidator.reset().parameter("agreementStatus").value(agreementStatus).notBlank().notExceedingLengthOf(50);

		final LocalDate startDate = fromApiJsonHelper.extractLocalDateNamed("startDate", element);
		baseDataValidator.reset().parameter("startDate").value(startDate).notBlank();
		


		final Long planCode = fromApiJsonHelper.extractLongNamed("planCode", element);
		baseDataValidator.reset().parameter("planCode").value(planCode).notBlank();

		final Long contractPeriod = fromApiJsonHelper.extractLongNamed("contractPeriod", element);
		baseDataValidator.reset().parameter("contractPeriod").value(contractPeriod).notBlank();

		final String paytermCode = fromApiJsonHelper.extractStringNamed("paytermCode",element);
		baseDataValidator.reset().parameter("paytermCode").value(paytermCode).notBlank();
		
		final JsonArray existAgreementDetails = fromApiJsonHelper.extractJsonArrayNamed("sourceData", element);
		 int DataSize = existAgreementDetails.size();
		 baseDataValidator.reset().parameter("sourceData").value(DataSize).integerGreaterThanZero(); 
		
		if (existAgreementDetails != null && existAgreementDetails.size() > 0) {
			String[] paramsDataArrayAttributes = null;
			paramsDataArrayAttributes = new String[existAgreementDetails.size()];

			for (int i = 0; i < existAgreementDetails.size(); i++) {

				paramsDataArrayAttributes[i] = existAgreementDetails.get(i).toString();
			}

			for (JsonElement jsonElement : existAgreementDetails) {

			    /*final Long source = fromApiJsonHelper.extractLongNamed("source", jsonElement);
				baseDataValidator.reset().parameter("source").value(source).notBlank();

				final String shareType = fromApiJsonHelper.extractStringNamed("shareType", jsonElement);
				baseDataValidator.reset().parameter("shareType").value(shareType).notBlank().notExceedingLengthOf(20);

				final BigDecimal shareAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("shareAmount",jsonElement);
				baseDataValidator.reset().parameter("shareAmount").value(shareAmount).notBlank();*/

				
				
			}
		}
		

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
