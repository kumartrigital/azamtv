package org.mifosplatform.crm.ticketmaster.serialization;

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
public class TicketMasterFromApiJsonDeserializer {
	
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("priority", "problemCode", "description", "assignedTo", "ticketDate", "ticketTime", "locale", "dateFormat",
														"type","subCategory","sourceOfTicket", "dueTime", "ticketURL","status","resolutionDescription", "fileLocation","statusCode","teamCode","teamUserId","title"));
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public TicketMasterFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	/**
	  * Validation for Create Ticket
	  * */
	 public void validateForCreate(final String json) {
		 
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
	     final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	     fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
	     final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	     final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("ticketmaster");
	        
	     final JsonElement element = fromApiJsonHelper.parse(json);
	        
	     final String priority = fromApiJsonHelper.extractStringNamed("priority", element);
	     final String problemCode = fromApiJsonHelper.extractStringNamed("problemCode", element);
	     final String description = fromApiJsonHelper.extractStringNamed("description", element);
	     final String assignedTo = fromApiJsonHelper.extractStringNamed("assignedTo", element);
	     final String subCategory = fromApiJsonHelper.extractStringNamed("subCategory", element); 
	     final String teamCode= fromApiJsonHelper.extractStringNamed("teamCode", element);
	     final Long teamUserId= fromApiJsonHelper.extractLongNamed("teamUserId", element);
	    
	     
	     baseDataValidator.reset().parameter("problemCode").value(problemCode).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("priority").value(priority).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("assignedTo").value(assignedTo).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("description").value(description).notBlank();
	     baseDataValidator.reset().parameter("subCategory").value(subCategory).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("teamCode").value(problemCode).notBlank().notExceedingLengthOf(15);
	     baseDataValidator.reset().parameter("teamUserId").value(problemCode).notBlank().notExceedingLengthOf(10);
	     
	        
	    throwExceptionIfValidationWarningsExist(dataValidationErrors);
	     
	 }
	 
	 /**
	  * Validation for Create Office Ticket
	  * */
	 public void validateForOfficeCreate(final String json) {
		 
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
	     final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	     fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
	     final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	     final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("ticketmaster");
	        
	     final JsonElement element = fromApiJsonHelper.parse(json);
	        
	     final String priority = fromApiJsonHelper.extractStringNamed("priority", element);
	     final String problemCode = fromApiJsonHelper.extractStringNamed("problemCode", element);
	     final String description = fromApiJsonHelper.extractStringNamed("description", element);
	     final String assignedTo = fromApiJsonHelper.extractStringNamed("assignedTo", element);
	     final String subCategory = fromApiJsonHelper.extractStringNamed("subCategory", element); 
	     final String teamCode= fromApiJsonHelper.extractStringNamed("teamCode", element);
	     final Long teamUserId= fromApiJsonHelper.extractLongNamed("teamUserId", element);
	     final String title= fromApiJsonHelper.extractStringNamed("title", element);
	     
	     baseDataValidator.reset().parameter("problemCode").value(problemCode).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("priority").value(priority).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("assignedTo").value(assignedTo).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("description").value(description).notBlank();
	     baseDataValidator.reset().parameter("title").value(title).notBlank();
	     baseDataValidator.reset().parameter("subCategory").value(subCategory).notBlank().notExceedingLengthOf(100);
	     baseDataValidator.reset().parameter("teamCode").value(problemCode).notBlank().notExceedingLengthOf(15);
	     baseDataValidator.reset().parameter("teamUserId").value(problemCode).notBlank().notExceedingLengthOf(10);
	     
	        
	    throwExceptionIfValidationWarningsExist(dataValidationErrors);
	     
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 /**
	  * Validation for Close Ticket
	  * */
	 public void validateForClose(final String json) {
	        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
	        
	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
	        
	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("ticketmaster");
	        
	        final JsonElement element = fromApiJsonHelper.parse(json);
	        
	        Integer status = null;
	        String resolutionDescription = null;
	        
	        if(fromApiJsonHelper.parameterExists("status", element)){
	        	status= Integer.parseInt( fromApiJsonHelper.extractStringNamed("status", element));
	        }
	        if(fromApiJsonHelper.parameterExists("resolutionDescription", element)){
	        	resolutionDescription = fromApiJsonHelper.extractStringNamed("resolutionDescription", element);
	        }
	        
	        baseDataValidator.reset().parameter("status").value(status).notBlank().notExceedingLengthOf(100);
	        baseDataValidator.reset().parameter("resolutionDescription").value(resolutionDescription).notBlank().notExceedingLengthOf(100);
	        
	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
		}

	 
	 private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	                "Validation errors exist.", dataValidationErrors);
	        }
	 }	
}