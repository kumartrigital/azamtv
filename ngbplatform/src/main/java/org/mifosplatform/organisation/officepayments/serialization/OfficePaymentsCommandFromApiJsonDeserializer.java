package org.mifosplatform.organisation.officepayments.serialization;

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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public class OfficePaymentsCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("paymentDate", "paymentCode","amountPaid","dateFormat",
					"locale", "remarks", "receiptNo", "chequeNo", "chequeDate", "bankName", "branchName","isChequeSelected","officePoId","isWallet","cancelRemark","officePoid","collectionBy","collectorName"));
	
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public OfficePaymentsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	/**
	 * @param json
	 * check validation for create office payment
	 */
	public void validateForCreate(final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("officepayment");

		final JsonElement element = fromApiJsonHelper.parse(json);
		
		final String paymentCode = fromApiJsonHelper.extractStringNamed("paymentCode", element);
		baseDataValidator.reset().parameter("paymentCode").value(paymentCode).notBlank().notExceedingLengthOf(100);
		
		final BigDecimal amountPaid = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountPaid", element);
		baseDataValidator.reset().parameter("amountPaid").value(amountPaid).notBlank().notExceedingLengthOf(100);
		
		final String receiptNo = fromApiJsonHelper.extractStringNamed("receiptNo", element);
		baseDataValidator.reset().parameter("receiptNo").value(receiptNo).notBlank().notExceedingLengthOf(50);
		
		
		if(fromApiJsonHelper.parameterExists("isChequeSelected", element)){
			final boolean isChequeSelected = fromApiJsonHelper.extractBooleanNamed("isChequeSelected", element);
			
			if(isChequeSelected){
				
				if(fromApiJsonHelper.parameterExists("chequeNo", element)){
					final String chequeNo = fromApiJsonHelper.extractStringNamed("chequeNo", element);
					baseDataValidator.reset().parameter("chequeNo").value(chequeNo).notBlank().notExceedingLengthOf(20);
				}
				if(fromApiJsonHelper.parameterExists("chequeDate", element)){
					final LocalDate chequeDate = fromApiJsonHelper.extractLocalDateNamed("chequeDate", element);
					baseDataValidator.reset().parameter("chequeDate").value(chequeDate).notBlank();
				}
				if(fromApiJsonHelper.parameterExists("bankName", element)){
					final String bankName = fromApiJsonHelper.extractStringNamed("bankName", element);
					baseDataValidator.reset().parameter("bankName").value(bankName).notBlank().notExceedingLengthOf(100);
				}
				if(fromApiJsonHelper.parameterExists("branchName", element)){
					final String branchName = fromApiJsonHelper.extractStringNamed("branchName", element);
					baseDataValidator.reset().parameter("branchName").value(branchName).notBlank().notExceedingLengthOf(100);
				}
				
				throwExceptionIfValidationWarningsExist(dataValidationErrors);
			}
		}

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	public void validateForCancel(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("payment");

		final JsonElement element = fromApiJsonHelper.parse(json);
		final String cancelRemark = fromApiJsonHelper.extractStringNamed("cancelRemark", element);
		baseDataValidator.reset().parameter("cancelRemark").value(cancelRemark).notBlank().notExceedingLengthOf(50);
		

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}
}