package org.mifosplatform.organisation.ippool.serialization;

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

/**
 * 
 * @author ashokreddy
 *
 */
@Component
public class IpPoolManagementCommandFromApiJsonDeserializer {


	private FromJsonHelper fromJsonHelper;

	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("ipPoolDescription","ipAddress","subnet","statusType","type","notes","clientId","ipAndSubnet","ipRange"));


	
	@Autowired
	public IpPoolManagementCommandFromApiJsonDeserializer(final FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}
	
	
	@SuppressWarnings("serial")
	public void validateForCreate(String json){
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource("ippool");;
		
		final JsonElement element = fromJsonHelper.parse(json);
		
		
		final String type = fromJsonHelper.extractStringNamed("type", element);
		baseValidatorBuilder.reset().parameter("type").value(type).notBlank();
		
		final String ipAddress = fromJsonHelper.extractStringNamed("ipAddress", element);
		baseValidatorBuilder.reset().parameter("ipAddress").value(ipAddress).notBlank().notExceedingLengthOf(16);
		
		final String subnet = fromJsonHelper.extractStringNamed("subnet", element);
		if(subnet != null && !(subnet.equalsIgnoreCase(""))){
			baseValidatorBuilder.reset().parameter("subnet").value(subnet).inMinMaxRange(0, 32);
		}
		
		
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }


	@SuppressWarnings("serial")
	public void validateForUpdate(String json) {
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
		
		final JsonElement element = fromJsonHelper.parse(json);
		
		final String statusType = fromJsonHelper.extractStringNamed("statusType", element);		
		baseValidatorBuilder.reset().parameter("statusType").value(statusType).notBlank().notExceedingLengthOf(2);
		
		final String notes = fromJsonHelper.extractStringNamed("notes", element);
		baseValidatorBuilder.reset().parameter("notes").value(notes).notExceedingLengthOf(60);
		
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
	@SuppressWarnings("serial")
	public void validateForUpdateDecription(String json){
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
		
		final JsonElement element = fromJsonHelper.parse(json);
		
		final String ipAndSubnet = fromJsonHelper.extractStringNamed("ipAndSubnet", element);		
		baseValidatorBuilder.reset().parameter("ipAndSubnet").value(ipAndSubnet).notBlank().notExceedingLengthOf(100);
	
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

}
