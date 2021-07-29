package org.mifosplatform.organisation.salescataloge.serialization;

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
public class SalesCatalogeCommandFromApiJsonDeserializer {

	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("name","planDetails","locale","catalogeId","salesPlanCategoryId"));
	
	
	@Autowired
	public SalesCatalogeCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
		
		final String name = fromJsonHelper.extractStringNamed("name", element);
		baseValidatorBuilder.reset().parameter("name").value(name).notNull().notExceedingLengthOf(100);
		
		final Long salesPlanCategoryId= fromJsonHelper.extractLongNamed("salesPlanCategoryId", element);
		baseValidatorBuilder.reset().parameter("salesPlanCategoryId").value(salesPlanCategoryId).notNull().notExceedingLengthOf(20);
		
		
		final String[] planDetails = fromJsonHelper.extractArrayNamed("planDetails", element);
		baseValidatorBuilder.reset().parameter("planDetails").value(planDetails).arrayNotEmpty();
        
		throwExceptionIfValidationWarningsExist(dataValidationErrors);		
			
	}

private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
            "Validation errors exist.", dataValidationErrors); }
}
}
