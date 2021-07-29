package org.mifosplatform.collectionbatch.unitofmeasurement.serialization;

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
public class UnitOfmeasurementCommandFromApiJsonDeserializer {
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("id","Name","Description","locale"));
	
	
	
	@Autowired
	public UnitOfmeasurementCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
		
		/*final Long uomId = this.fromJsonHelper.extractLongNamed("uomId", element);
		baseValidatorBuilder.reset().parameter("uomId").value(uomId).notNull().notExceedingLengthOf(100);*/
		
		final String Name = fromJsonHelper.extractStringNamed("Name", element);
		baseValidatorBuilder.reset().parameter("Name").value(Name).notNull().notExceedingLengthOf(100);
		
		
		final String Description = fromJsonHelper.extractStringNamed("Description", element);
		baseValidatorBuilder.reset().parameter("Description").value(Description).notNull().notExceedingLengthOf(100);
		
		this.throwExceptionIfValidationWarningsExist(dataValidationErrors);	
		
		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
            "Validation errors exist.", dataValidationErrors); }
	}
		
	

}
