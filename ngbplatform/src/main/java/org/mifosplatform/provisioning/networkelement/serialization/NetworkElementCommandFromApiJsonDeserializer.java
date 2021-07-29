package org.mifosplatform.provisioning.networkelement.serialization;

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
public class NetworkElementCommandFromApiJsonDeserializer {
	
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("systemcode","systemname","status","locale","isGroupSupported"));
	
	
	@Autowired
	public NetworkElementCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	
	public void validateForCreate(String json) {
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
		
		final JsonElement element = this.fromJsonHelper.parse(json);
		
		
		final String systemcode = fromJsonHelper.extractStringNamed("systemcode", element);
		baseValidatorBuilder.reset().parameter("systemcode").value(systemcode).notNull().notExceedingLengthOf(100);
		
		final String systemname = fromJsonHelper.extractStringNamed("systemname", element);
		baseValidatorBuilder.reset().parameter("systemname").value(systemname).notNull().notExceedingLengthOf(150);
		
	    final String status = fromJsonHelper.extractStringNamed("status", element);
		baseValidatorBuilder.reset().parameter("status").value(status).notNull().notExceedingLengthOf(150);
		
	/*		
		final String channelType = this.fromJsonHelper.extractStringNamed("channelType", element);
		baseValidatorBuilder.reset().parameter("channelType").value(channelType).notNull().notExceedingLengthOf(10);
		
		final boolean isLocalChannel = this.fromJsonHelper.extractBooleanNamed("isLocalChannel", element);
		baseValidatorBuilder.reset().parameter("isLocalChannel").value(isLocalChannel);
		
		final boolean isHdChannel = this.fromJsonHelper.extractBooleanNamed("isHdChannel", element);
		baseValidatorBuilder.reset().parameter("isHdChannel").value(isHdChannel);

		final Long channelSequence= fromJsonHelper.extractLongNamed("channelSequence", element);
		baseValidatorBuilder.reset().parameter("channelSequence").value(channelSequence).notNull().notExceedingLengthOf(10);
		
		final Long broadcasterId= fromJsonHelper.extractLongNamed("broadcasterId", element);
		baseValidatorBuilder.reset().parameter("broadcasterId").value(broadcasterId).notNull().notExceedingLengthOf(20);*/
		
	    
		throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		
		
	}
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}

}
