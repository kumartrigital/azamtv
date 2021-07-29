package org.mifosplatform.portfolio.provisioncodemapping.serialization;

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
public class ProvisionCodeMappingCommandFromApiJsonDeserializer {
	
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("provisionCode","networkCode","provisionValue","locale"));
	
	@Autowired
	public ProvisionCodeMappingCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}
	
	public void validateForCreate(String json) {
		

		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
	      }
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("dataValidationErrors");
	
        final JsonElement element = this.fromJsonHelper.parse(json);
        
        final String provisionCode = fromJsonHelper.extractStringNamed("provisionCode", element);
    	baseDataValidator.reset().parameter("provisionCode").value(provisionCode).notNull().notExceedingLengthOf(25);
    	
    	final String networkCode = fromJsonHelper.extractStringNamed("networkCode", element);
    	baseDataValidator.reset().parameter("networkCode").value(networkCode).notNull().notExceedingLengthOf(25);
    
    	final String provisionValue = fromJsonHelper.extractStringNamed("provisionValue", element);
    	baseDataValidator.reset().parameter("provisionValue").value(provisionValue).notNull().notExceedingLengthOf(25);
    	
    	
    	throwExceptionIfValidationWarningsExist(dataValidationErrors);
	
		
	}

	private void throwExceptionIfValidationWarningsExist(List<ApiParameterError> dataValidationErrors) {
		
		 if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	                "Validation errors exist.", dataValidationErrors); }
		
	}

}
