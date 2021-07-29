package org.mifosplatform.portfolio.client.serialization;

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
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;

@Component
public class ClientBillInfoCommandFromApiJsonDeserializer {

	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("billDayOfMonth","billCurrency","billFrequency","billSegment","dateFormat","nextBillDay","lastBillDay","lastBillNo","paymentType",
			"billSuppressionFlag","billSuppressionId","firstBill","hotBill","locale"));
	
	
	
	
	@Autowired
	public ClientBillInfoCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
		

		final Long billDayOfMonth = this.fromJsonHelper.extractLongNamed("billDayOfMonth", element);
		baseValidatorBuilder.reset().parameter("billDayOfMonth").value(billDayOfMonth).notNull().notExceedingLengthOf(10);
		
		final Long billCurrency = this.fromJsonHelper.extractLongNamed("billCurrency", element);
		baseValidatorBuilder.reset().parameter("billCurrency").value(billCurrency).notNull().notExceedingLengthOf(100);
		
		final Long billFrequency = fromJsonHelper.extractLongNamed("billFrequency", element);
		baseValidatorBuilder.reset().parameter("billFrequency").value(billFrequency).notNull().notExceedingLengthOf(100);
		
		final String billSegment = fromJsonHelper.extractStringNamed("billSegment", element);
		baseValidatorBuilder.reset().parameter("billSegment").value(billSegment).notExceedingLengthOf(100);
		
		final LocalDate nextBillDay = fromJsonHelper.extractLocalDateNamed("nextBillDay", element);
		baseValidatorBuilder.reset().parameter("nextBillDay").value(nextBillDay).notExceedingLengthOf(100);
		
		final LocalDate lastBillDay = fromJsonHelper.extractLocalDateNamed("lastBillDay", element);
		baseValidatorBuilder.reset().parameter("lastBillDay").value(lastBillDay).notExceedingLengthOf(100);
		
		final Long lastBillNo = this.fromJsonHelper.extractLongNamed("lastBillNo", element);
		baseValidatorBuilder.reset().parameter("lastBillNo").value(lastBillNo).notNull().notExceedingLengthOf(10);
		
		final Long paymentType = this.fromJsonHelper.extractLongNamed("paymentType", element);
		baseValidatorBuilder.reset().parameter("paymentType").value(paymentType).notNull();
		
		final boolean billSuppressionFlag = this.fromJsonHelper.extractBooleanNamed("billSuppressionFlag", element);
		baseValidatorBuilder.reset().parameter("billSuppressionFlag").value(billSuppressionFlag).notNull();
		
		final Long billSuppressionId = this.fromJsonHelper.extractLongNamed("billSuppressionId", element);
		baseValidatorBuilder.reset().parameter("billSuppressionId").value(billSuppressionId).notNull();
		
		final boolean firstBill = this.fromJsonHelper.extractBooleanNamed("firstBill", element);
		baseValidatorBuilder.reset().parameter("firstBill").value(firstBill).notNull();
		
		final boolean hotBill = this.fromJsonHelper.extractBooleanNamed("hotBill", element);
		baseValidatorBuilder.reset().parameter("hotBill").value(hotBill).notNull();
		
		
		
	    this.throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}
	
	
	
}
