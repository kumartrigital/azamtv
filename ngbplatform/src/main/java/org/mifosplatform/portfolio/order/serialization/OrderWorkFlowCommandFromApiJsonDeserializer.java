package org.mifosplatform.portfolio.order.serialization;

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
public class OrderWorkFlowCommandFromApiJsonDeserializer {
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("orderId","status","assignedTo","description","locale","clientId","clientServiceId","teamEmail"));
	
	
	@Autowired
	public OrderWorkFlowCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
		
		
		final Long orderId = fromJsonHelper.extractLongNamed("orderId", element);
		baseValidatorBuilder.reset().parameter("orderId").value(orderId);
		
		final String status = fromJsonHelper.extractStringNamed("status", element);
		baseValidatorBuilder.reset().parameter("status").value(status).notNull();
		
	    final Long assignedTo = fromJsonHelper.extractLongNamed("assignedTo", element);
		baseValidatorBuilder.reset().parameter("assignedTo").value(assignedTo).notNull();
		
		
		
		final String description = this.fromJsonHelper.extractStringNamed("description", element);
		baseValidatorBuilder.reset().parameter("description").value(description);
		
	    
		throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		
		
	}
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}

}
