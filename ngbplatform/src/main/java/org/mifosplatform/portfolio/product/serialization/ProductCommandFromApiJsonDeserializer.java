package org.mifosplatform.portfolio.product.serialization;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ProductCommandFromApiJsonDeserializer {

	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("productCode","productDescription",
    		"productCategory","serviceId",/*"provisionId","validityStart","validityEnd",*/"status","isBouquet","locale","dateFormat","productArray",
    		"serviceCategory","serviceCode","serviceDescription","serviceType","isAutoProvision","serviceArray"));
	
	@Autowired
	public ProductCommandFromApiJsonDeserializer(FromJsonHelper fromJsonHelper) {
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
        
        final String productCode = fromJsonHelper.extractStringNamed("productCode", element);
    	baseDataValidator.reset().parameter("productCode").value(productCode).notNull().notExceedingLengthOf(15);
    	
    	final String productDescription = fromJsonHelper.extractStringNamed("productDescription", element);
    	baseDataValidator.reset().parameter("productDescription").value(productDescription).notNull().notExceedingLengthOf(100);
	
    	//final boolean productCategory = fromJsonHelper.extractBooleanNamed("productCategory", element);
    	//baseDataValidator.reset().parameter("productCategory").value(productCategory).notNull().notExceedingLengthOf(10);
    
    	final Long serviceId = fromJsonHelper.extractLongNamed("serviceId", element);
    	baseDataValidator.reset().parameter("serviceId").value(serviceId).notNull().notExceedingLengthOf(20);
    	
    	/*final Long provisionId = fromJsonHelper.extractLongNamed("provisionId", element);
    	baseDataValidator.reset().parameter("provisionId").value(provisionId).notNull().notExceedingLengthOf(10);
    	*/
        //final String validityStart = fromJsonHelper.extractStringNamed("validityStart", element);
    	//baseDataValidator.reset().parameter("validityStart").value(validityStart).notNull();
    	
    	//final String validityEnd = fromJsonHelper.extractStringNamed("validityEnd", element);
    	//baseDataValidator.reset().parameter("validityEnd").value(validityEnd).notNull();
    	
    	 /*final LocalDate validityStart = fromJsonHelper.extractLocalDateNamed("validityStart", element);
         baseDataValidator.reset().parameter("validityStart").value(validityStart).notNull();
    	
    	 final LocalDate validityEnd = fromJsonHelper.extractLocalDateNamed("validityEnd", element);
         baseDataValidator.reset().parameter("validityEnd").value(validityEnd).notNull();*/
    	
    	final String status = fromJsonHelper.extractStringNamed("status", element);
    	baseDataValidator.reset().parameter("status").value(status).notNull();
    	
    	throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}



	private void throwExceptionIfValidationWarningsExist(List<ApiParameterError> dataValidationErrors) {

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    
	}

	
	
	
}
