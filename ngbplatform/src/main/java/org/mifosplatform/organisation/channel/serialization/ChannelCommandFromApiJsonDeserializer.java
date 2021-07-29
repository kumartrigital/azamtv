package org.mifosplatform.organisation.channel.serialization;

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
public class ChannelCommandFromApiJsonDeserializer {
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("channelName","channelCategory","channelType","isLocalChannel","isLocalChanne","isHdChannel","channelSequence","broadcasterId","locale","language","broadcasterName"));
	
	
	@Autowired
	public ChannelCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
		
		
		final String channelName = fromJsonHelper.extractStringNamed("channelName", element);
		baseValidatorBuilder.reset().parameter("channelName").value(channelName).notNull().notExceedingLengthOf(100);
		
		final String channelCategory = fromJsonHelper.extractStringNamed("channelCategory", element);
		baseValidatorBuilder.reset().parameter("channelCategory").value(channelCategory).notNull().notExceedingLengthOf(150);
		
	    final String language = fromJsonHelper.extractStringNamed("language", element);
		baseValidatorBuilder.reset().parameter("language").value(language).notNull().notExceedingLengthOf(150);
		
		
		
		final String channelType = this.fromJsonHelper.extractStringNamed("channelType", element);
		baseValidatorBuilder.reset().parameter("channelType").value(channelType).notNull().notExceedingLengthOf(10);
		
		//final boolean isLocalChannel = this.fromJsonHelper.extractBooleanNamed("isLocalChannel", element);
		//baseValidatorBuilder.reset().parameter("isLocalChannel").value(isLocalChannel);
		
		//final boolean isHdChannel = this.fromJsonHelper.extractBooleanNamed("isHdChannel", element);
		//baseValidatorBuilder.reset().parameter("isHdChannel").value(isHdChannel);

		final Long channelSequence= fromJsonHelper.extractLongNamed("channelSequence", element);
		baseValidatorBuilder.reset().parameter("channelSequence").value(channelSequence).notNull().notExceedingLengthOf(10);
		
		final Long broadcasterId= fromJsonHelper.extractLongNamed("broadcasterId", element);
		baseValidatorBuilder.reset().parameter("broadcasterId").value(broadcasterId).notNull().notExceedingLengthOf(20);
		
	    
		throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		
		
	}
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}

}
