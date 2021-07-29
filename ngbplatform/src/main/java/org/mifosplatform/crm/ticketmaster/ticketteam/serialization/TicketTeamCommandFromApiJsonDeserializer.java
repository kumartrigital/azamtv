package org.mifosplatform.crm.ticketmaster.ticketteam.serialization;

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
public class TicketTeamCommandFromApiJsonDeserializer {
	
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("userId","teamCode","teamDescription","teamCategory","status","teamEmail","locale"));
			
	
	@Autowired
	public TicketTeamCommandFromApiJsonDeserializer(
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
		

		final Long userId = this.fromJsonHelper.extractLongNamed("userId", element);
		baseValidatorBuilder.reset().parameter("userId").value(userId).notNull().notExceedingLengthOf(20);
		
		final String teamCode = this.fromJsonHelper.extractStringNamed("teamCode", element);
		baseValidatorBuilder.reset().parameter("teamCode").value(teamCode).notNull().notExceedingLengthOf(100);
		
		final String teamDescription = fromJsonHelper.extractStringNamed("teamDescription", element);
		baseValidatorBuilder.reset().parameter("teamDescription").value(teamDescription).notNull().notExceedingLengthOf(100);
		
		final String teamCategory = fromJsonHelper.extractStringNamed("teamCategory", element);
		baseValidatorBuilder.reset().parameter("teamCategory").value(teamCategory).notNull().notExceedingLengthOf(1);
		
		final String status = fromJsonHelper.extractStringNamed("status", element);
		baseValidatorBuilder.reset().parameter("status").value(status).notNull().notExceedingLengthOf(100);
		
		
		
	    this.throwExceptionIfValidationWarningsExist(dataValidationErrors);		
		}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	    if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	            "Validation errors exist.", dataValidationErrors); }
	}

}
