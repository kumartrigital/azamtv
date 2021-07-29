package org.mifosplatform.crm.ticketmaster.ticketmapping.serialization;

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
public class TicketMappingCommandFromApiJsonDeserializer {

	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("teamId","userId","userRole","isTeamLead","status","locale","userDetails"));
			
	
	@Autowired
	public TicketMappingCommandFromApiJsonDeserializer(
			FromJsonHelper fromJsonHelper) {
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
		

		final Long teamId = this.fromJsonHelper.extractLongNamed("teamId", element);
		baseValidatorBuilder.reset().parameter("teamId").value(teamId).notNull().notExceedingLengthOf(20);
		

		final String[] userId = fromJsonHelper.extractArrayNamed("userId", element);
		baseValidatorBuilder.reset().parameter("userId").value(userId).arrayNotEmpty();
		
		/*final Long userId = this.fromJsonHelper.extractLongNamed("userId", element);
		baseValidatorBuilder.reset().parameter("userId").value(userId).notNull().notExceedingLengthOf(20);*/
		
		/*final String userRole = fromJsonHelper.extractStringNamed("userRole", element);
		baseValidatorBuilder.reset().parameter("userRole").value(userRole).notNull().notExceedingLengthOf(1);*/
		
		/*final String isTeamLead = fromJsonHelper.extractStringNamed("isTeamLead", element);
		baseValidatorBuilder.reset().parameter("isTeamLead").value(isTeamLead).notNull().notExceedingLengthOf(1);*/
		
		/*final String status = fromJsonHelper.extractStringNamed("status", element);
		baseValidatorBuilder.reset().parameter("status").value(status).notNull().notExceedingLengthOf(20);*/
		
		
	    this.throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}
}
