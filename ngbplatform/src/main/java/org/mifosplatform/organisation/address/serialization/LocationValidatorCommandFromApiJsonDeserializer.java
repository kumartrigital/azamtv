package org.mifosplatform.organisation.address.serialization;

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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class LocationValidatorCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("entityCode", "entityName", "parentEntityId"));
	
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public LocationValidatorCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	public void validateForCreate(final String json, final String entityType) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		final String entityVal = entityType.toLowerCase();
		final String dynamicEntityCode = entityType.toLowerCase()+"Code";
		final String dynamicEntityName = entityType.toLowerCase()+"Name";
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("addressMaster."+entityVal);

		final JsonElement element = fromApiJsonHelper.parse(json);
		
		final String entityCode = fromApiJsonHelper.extractStringNamed("entityCode", element);
		
		if(entityVal.equalsIgnoreCase("country")){
			baseDataValidator.reset().parameter(dynamicEntityCode).value(entityCode)
			.notBlank().notExceedingLengthOf(4);
			
		}else{
			baseDataValidator.reset().parameter(dynamicEntityCode).value(entityCode)
			.notBlank().notExceedingLengthOf(10);
		}
		if(entityCode != null && entityCode !=""){
		if ( !entityCode.matches("[a-zA-Z]+")){

			dataValidationErrors.add(ApiParameterError.parameterError(dynamicEntityCode+" should contain only characters", "entityCode", "dynamicEntityCode"));

			 } 
		}
		final String entityName = fromApiJsonHelper.extractStringNamed("entityName", element);

		
		baseDataValidator.reset().parameter(dynamicEntityName).value(entityName)
		.notBlank().notExceedingLengthOf(100);
		
		

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
}
