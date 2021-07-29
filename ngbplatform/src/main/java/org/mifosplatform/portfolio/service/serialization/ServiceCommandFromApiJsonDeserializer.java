package org.mifosplatform.portfolio.service.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class ServiceCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("serviceCode","serviceDescription",
    		"serviceUnitType","serviceType","status","isOptional","isAutoProvision","serviceCategory","serviceArray"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ServiceCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("service");

        final JsonElement element = fromApiJsonHelper.parse(json);
        
        	final String serviceCode = fromApiJsonHelper.extractStringNamed("serviceCode", element);
        	baseDataValidator.reset().parameter("serviceCode").value(serviceCode).notBlank().notExceedingLengthOf(10);
        	final String serviceDescription = fromApiJsonHelper.extractStringNamed("serviceDescription", element);
        	baseDataValidator.reset().parameter("serviceDescription").value(serviceDescription).notBlank();
        	final String  serviceType = fromApiJsonHelper.extractStringNamed("serviceType", element);
        	baseDataValidator.reset().parameter("serviceType").value(serviceType).notBlank().notExceedingLengthOf(100);
        	/*final String  serviceUnitType = fromApiJsonHelper.extractStringNamed("serviceUnitType", element);
        	baseDataValidator.reset().parameter("serviceUnitType").value(serviceUnitType).notBlank();*/
        	final String  status = fromApiJsonHelper.extractStringNamed("status", element);
        	baseDataValidator.reset().parameter("status").value(status).notBlank();
        	final String serviceCategory = this.fromApiJsonHelper.extractStringNamed("serviceCategory", element);
        	baseDataValidator.reset().parameter("serviceCategory").value(serviceCategory).notBlank().notExceedingLengthOf(1);
   
   
        	
        	//validating details
        	final JsonArray detailsArray = fromApiJsonHelper.extractJsonArrayNamed("serviceArray", element);
            String[] details = new String[detailsArray.size()];
            final int detailsArraySize = detailsArray.size();
            baseDataValidator.reset().parameter("serviceArray").value(detailsArraySize).integerGreaterThanZero();
            
            if(detailsArraySize > 0){
	    	    for(int i = 0; i < detailsArray.size(); i++){
	    	    	details[i] = detailsArray.get(i).toString();
	    	    }
	    	    for (final String detail : details) {
	    	    	
	    	    	final JsonElement detailElement = fromApiJsonHelper.parse(detail);
	    	    
	    	    	final String paramCategory = this.fromApiJsonHelper.extractStringNamed("paramCategory", detailElement);
	    	    	baseDataValidator.reset().parameter("paramCategory").value(paramCategory).notNull();
	    	    	
	    	    	final String paramName = this.fromApiJsonHelper.extractStringNamed("paramName", detailElement);
	    	    	baseDataValidator.reset().parameter("paramName").value(paramName).notNull();
	    	    	
	    	    	final String paramType = this.fromApiJsonHelper.extractStringNamed("paramType", detailElement);
	    	    	baseDataValidator.reset().parameter("paramType").value(paramType).notNull();
	    	    	
	    	    	/*final String paramValue = this.fromApiJsonHelper.extractStringNamed("paramValue", detailElement);
	    	    	baseDataValidator.reset().parameter("paramValue").value(paramValue).notNull();
	    	    	*/
	    	    }
            }
        	
        	
        	
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");

        final JsonElement element = fromApiJsonHelper.parse(json);
        if (fromApiJsonHelper.parameterExists("name", element)) {
            final String name = fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }
        
      //validating details
    	final JsonArray detailsArray = fromApiJsonHelper.extractJsonArrayNamed("serviceArray", element);
        String[] details = new String[detailsArray.size()];
        final int detailsArraySize = detailsArray.size();
        baseDataValidator.reset().parameter("serviceArray").value(detailsArraySize).integerGreaterThanZero();
        
        if(detailsArraySize > 0){
    	    for(int i = 0; i < detailsArray.size(); i++){
    	    	details[i] = detailsArray.get(i).toString();
    	    }
    	    for (final String detail : details) {
    	    	
    	    	final JsonElement detailElement = fromApiJsonHelper.parse(detail);
    	    
    	    	final String paramCategory = this.fromApiJsonHelper.extractStringNamed("paramCategory", detailElement);
    	    	baseDataValidator.reset().parameter("paramCategory").value(paramCategory).notNull();
    	    	
    	    	final String paramName = this.fromApiJsonHelper.extractStringNamed("paramName", detailElement);
    	    	baseDataValidator.reset().parameter("paramName").value(paramName).notNull();
    	    	
    	    	final String paramType = this.fromApiJsonHelper.extractStringNamed("paramType", detailElement);
    	    	baseDataValidator.reset().parameter("paramType").value(paramType).notNull();
    	    	
    	    	final String paramValue = this.fromApiJsonHelper.extractStringNamed("paramValue", detailElement);
    	    	baseDataValidator.reset().parameter("paramValue").value(paramValue).notNull();
    	    	
    	    }
        }
        

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}