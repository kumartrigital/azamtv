package org.mifosplatform.organisation.broadcaster.serialization;

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

@Component
public class BroadcasterCommandFromApiJsonDeserializer {

	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("broadcasterCode","broadcasterName","contactMobile","contactNumber","contactName","email","address","pin","locale"));
	
	
	
	@Autowired
	public BroadcasterCommandFromApiJsonDeserializer(
			FromJsonHelper fromJsonHelper) {
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
		

		final String broadcasterCode = this.fromJsonHelper.extractStringNamed("broadcasterCode", element);
		baseValidatorBuilder.reset().parameter("broadcasterCode").value(broadcasterCode).notNull().notExceedingLengthOf(10);
		
		final String broadcasterName = this.fromJsonHelper.extractStringNamed("broadcasterName", element);
		baseValidatorBuilder.reset().parameter("broadcasterName").value(broadcasterName).notNull().notExceedingLengthOf(100);
		
		final Long contactMobile = fromJsonHelper.extractLongNamed("contactMobile", element);
		baseValidatorBuilder.reset().parameter("contactMobile").value(contactMobile).notNull().notExceedingLengthOf(100);
		
		
		final Long contactNumber = fromJsonHelper.extractLongNamed("contactNumber", element);
		baseValidatorBuilder.reset().parameter("contactNumber").value(contactNumber).notNull().notExceedingLengthOf(100);
		
		
		final String contactName = fromJsonHelper.extractStringNamed("contactName", element);
		baseValidatorBuilder.reset().parameter("contactName").value(contactName).notNull().notExceedingLengthOf(100);
		
		
		final String email = fromJsonHelper.extractStringNamed("email", element);
		baseValidatorBuilder.reset().parameter("email").value(email).notNull().notExceedingLengthOf(100);
		
		
		final String address = fromJsonHelper.extractStringNamed("address", element);
		baseValidatorBuilder.reset().parameter("address").value(address).notNull().notExceedingLengthOf(250);
		
		
		final String pin = fromJsonHelper.extractStringNamed("pin", element);
		if ( !pin.matches("[a-zA-Z0-9]+")){

			dataValidationErrors.add(ApiParameterError.parameterError(" special characters not allowed in GST Number"," special characters not allowed in GST Number" , "pin" ,pin));

			 }
		baseValidatorBuilder.reset().parameter("pin").value(pin).notNull().notExceedingLengthOf(100);
		
		
		
	    this.throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}
	
}
