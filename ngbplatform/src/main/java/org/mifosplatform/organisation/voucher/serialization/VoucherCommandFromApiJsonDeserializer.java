package org.mifosplatform.organisation.voucher.serialization;

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
public class VoucherCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("id", "batchName", "length",
					"beginWith", "pinCategory", "pinType", "quantity","serialNo", "expiryDate", "dateFormat", "pinValue",
					"pinNO", "locale", "pinExtention","officeId","priceId","status","voucherIds","cancelReason","batchType","orderStatus"));
	
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public VoucherCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

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
				dataValidationErrors).resource("voucher");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String batchName = fromApiJsonHelper.extractStringNamed("batchName", element);
		baseDataValidator.reset().parameter("batchName").value(batchName).notBlank();

		final Long length = fromApiJsonHelper.extractLongNamed("length", element);
	    baseDataValidator.reset().parameter("length").value(length).notNull().notLessThanMin(1).notGreaterThanMax(60);
	    
	    final String beginWith = fromApiJsonHelper.extractStringNamed("beginWith", element);
		baseDataValidator.reset().parameter("beginWith").value(beginWith);

		final String pinCategory = fromApiJsonHelper.extractStringNamed("pinCategory", element);
		baseDataValidator.reset().parameter("pinCategory").value(pinCategory).notBlank();
		
		final Long quantity = fromApiJsonHelper.extractLongNamed("quantity", element);
	    baseDataValidator.reset().parameter("quantity").value(quantity).notNull().notLessThanMin(1);
	    
		final Long serialNo = fromApiJsonHelper.extractLongNamed("serialNo", element);
	    baseDataValidator.reset().parameter("serialNo").value(serialNo).notNull().inMinMaxRange(0, 18);
		
		final String pinType = fromApiJsonHelper.extractStringNamed("pinType",element);
		baseDataValidator.reset().parameter("pinType").value(pinType).notBlank();

		BigDecimal pinValue = null ;

		if(pinType != null){
			if(pinType.equalsIgnoreCase("VALUE")){
				pinValue = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("pinValue", element);
				baseDataValidator.reset().parameter("pinValue").value(pinValue).notNull();
			}
			else if(pinType.equalsIgnoreCase("PRODUCT")){
			
				pinValue = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("pinValue", element);
				baseDataValidator.reset().parameter("productValue").value(pinValue).notNull();
			}
			else if(pinType.equalsIgnoreCase("COUPON")){
				
				pinValue = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("pinValue", element);
				baseDataValidator.reset().parameter("promotionValue").value(pinValue).notNull();
			}
		}

	  
		final LocalDate ExpiryDate = fromApiJsonHelper.extractLocalDateNamed("expiryDate", element);
		baseDataValidator.reset().parameter("expiryDate").value(ExpiryDate).notBlank();
		
		if (null!=serialNo && null!=quantity && null!=length && null!=pinValue) {

			if (serialNo.longValue() > 0 && quantity.longValue() > 0 && length.longValue() > 0 && pinValue.longValue() > 0) {

				String minSerialSeries = "";
				String maxSerialSeries = "";
				for (int x = 0; x < serialNo.longValue(); x++) {
					if (x == 0) {
						minSerialSeries += "1";
						maxSerialSeries += "9";
					} else {
						maxSerialSeries += "9";
					}
				}

				BigDecimal minNo = new BigDecimal(minSerialSeries);//Long.parseLong(minSerialSeries);
				BigDecimal maxNo = new BigDecimal(maxSerialSeries);//Long.parseLong(maxSerialSeries);
				baseDataValidator.reset().parameter("quantity").value(quantity.longValue()).
				inMinAndMaxAmountRange(minNo, maxNo);
				
				//baseDataValidator.reset().parameter("beginWith").value(beginWith.length()).notGreaterThanMax(length);
				baseDataValidator.reset().parameter("pinValue").value(pinValue.longValue()).inMinMaxRange(1, 1000000000);
			}
		}
		
		final Long officeId = fromApiJsonHelper.extractLongNamed("officeId", element);
        baseDataValidator.reset().parameter("officeId").value(officeId).notNull().integerGreaterThanZero();

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
	
public void validateForUpdate(final String json, Boolean isUpdateVoucher) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("voucher");

		final JsonElement element = fromApiJsonHelper.parse(json);
		if(isUpdateVoucher.equals(true)){
			final String status = fromApiJsonHelper.extractStringNamed("status", element);
			baseDataValidator.reset().parameter("status").value(status).notBlank();
		}
		
		final String[] voucherIds = fromApiJsonHelper.extractArrayNamed("voucherIds", element);
        baseDataValidator.reset().parameter("voucherIds").value(voucherIds).arrayNotEmpty();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

public void validateForCancel(String json, boolean b) {
	
	if (StringUtils.isBlank(json)) {
		throw new InvalidJsonException();
	}

	final Type typeOfMap = new TypeToken<Map<String, Object>>() {
	}.getType();
	
	fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("voucher");

	 final JsonElement element = fromApiJsonHelper.parse(json);
		final String cancelReason = fromApiJsonHelper.extractStringNamed("cancelReason", element);
		baseDataValidator.reset().parameter("cancelReason").value(cancelReason).notBlank();
	throwExceptionIfValidationWarningsExist(dataValidationErrors);

}

}
